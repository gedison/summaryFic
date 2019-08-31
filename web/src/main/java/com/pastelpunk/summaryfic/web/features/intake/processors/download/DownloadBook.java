package com.pastelpunk.summaryfic.web.features.intake.processors.download;

import com.pastelpunk.summaryfic.core.features.intake.job.IntakeJobRepository;
import com.pastelpunk.summaryfic.core.features.intake.task.IntakeJobTaskRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeStatus;
import com.pastelpunk.summaryfic.core.models.raw.AO3TagKey;
import com.pastelpunk.summaryfic.core.models.raw.Book;
import com.pastelpunk.summaryfic.core.models.raw.Chapter;
import com.pastelpunk.summaryfic.core.models.raw.Tag;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.xsoup.Xsoup;

import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
//TODO - Refactor this class +
// the other one that uses jsoup and update the route to allow other sources
public class DownloadBook extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadBook.class);

    private static final String TITLE_XML = "//h2[contains(@class, 'title heading')]";
    private static final String AUTHOR_XML = "//h3[contains(@class, 'byline heading')]";
    private static final String CHAPTER_SUMMARY_XML = "//blockquote[contains(@class, 'userstuff')]";
    private static final String CHAPTER_LIST_ID = "selected_id";
    private static final String TEXT_XML = "//div[@id='chapters']/div[@class='userstuff']";
    private static final String TEXT_XML_CHAPTERED = "//div[@id='chapters']/div/div[@class='userstuff module']/p";

    private final IntakeJobTaskRepository intakeJobTaskRepository;

    public DownloadBook(IntakeJobTaskRepository intakeJobTaskRepository){
        this.intakeJobTaskRepository = intakeJobTaskRepository;
    }

    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed to download book {}", e);
        RestExchange<IntakeJobTask, Void> restExchange = new RestExchange<>(exchange);
        var intakeJob = restExchange.getInputObject();
        intakeJob.setStatus(IntakeStatus.ERROR.name());
        intakeJob.setStatusMessage(e.getMessage());
        intakeJobTaskRepository.updateIntakeJobTask(intakeJob);
    }

    protected void execute(Exchange exchange) throws Exception {
        RestExchange<IntakeJobTask, Book> restExchange = new RestExchange<>(exchange);

        var intakeJob = restExchange.getInputObject();
        restExchange.set(IntakeConstants.JOB_STATUS, intakeJob);

        var url = "https://archiveofourown.org"+intakeJob.getUri();
        Book book = downloadBook(url);

        book.setIntakeJobId(intakeJob.getIntakeJobId());
        book.setSource(intakeJob.getSource());
        book.setUri(intakeJob.getUri());

        restExchange.setOutputObject(book);
        restExchange.syncHeaders();
    }


    public Book downloadBook(String url) throws Exception{
        Book ret = new Book();
        URL mUrl = new URL(url);
        URLConnection connection = mUrl.openConnection();
        String out = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
        Document document = Jsoup.parse(out);

        var chapterUrl = url;
        if(document.getElementsByTag("body").text().contains("You are being redirected.")){
            chapterUrl = document.getElementsByTag("a").attr("href");
            chapterUrl = chapterUrl.replace("http", "https");
            mUrl = new URL(chapterUrl);
            connection = mUrl.openConnection();
            out = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
            document = Jsoup.parse(out);
        }

        ret.setTitle(Xsoup.compile(TITLE_XML).evaluate(document).getElements().text());
        ret.setAuthor(Xsoup.compile(AUTHOR_XML).evaluate(document).getElements().text());

        var isSingleChapter = false;
        Element select = document.getElementById(CHAPTER_LIST_ID);
        List<Chapter> chapters = new ArrayList<>();
        if(Objects.nonNull(select)){
            Elements elements = select.getAllElements();
            List<String> ids = elements.stream().map(Element::val).distinct().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            ids.forEach(chapterId->{
                try {
                    chapters.add(downloadChapter(url + "/chapters/" + chapterId , false));
                }catch (Exception e){
                    LOGGER.info("{}",e.getMessage(),e);
                }
            });
        }else{
            try {
                if(chapterUrl.contains("chapters")){
                    chapters.add(downloadChapter(url , false));
                }else{
                    isSingleChapter = true;
                    chapters.add(downloadChapter(url , true));
                }
            }catch (Exception e){
                LOGGER.info("{}",e.getMessage(),e);
            }
        }

        setTags(document, ret, isSingleChapter);

        int i = 0;
        for(Chapter chapter : chapters){
            chapter.setOrdering(i++);
        }

        ret.setChapters(chapters);
        return ret;
    }

    private void setTags(Document document, Book book, boolean isSingleChapter){

        List<Tag> tags = new ArrayList<>();

        var tagGroups = Arrays.asList("rating tags","warning tags","category tags","fandom tags",
                "relationship tags","character tags","freeform tags");
        var keys = Arrays.asList(AO3TagKey.RATING.name(), AO3TagKey.ARCHIVE_WARNING.name(), AO3TagKey.CATEGORY.name(),
                AO3TagKey.FANDOM.name(), AO3TagKey.RELATIONSHIP.name(), AO3TagKey.CHARACTER.name(), AO3TagKey.ADDITIONAL_TAG.name());

        var tag = (isSingleChapter) ? "/html/body/div/div/div/div/dl/dd[@class='${tagGroup}']/ul/li/a" :
                "/html/body/div/div/div/div/div/dl/dd[@class='${tagGroup}']/ul/li/a";
        for(int i=0; i<tagGroups.size(); i++){
            var group = tagGroups.get(i);
            var key = keys.get(i);
            var xPath = tag.replace("${tagGroup}", group);
            tags.addAll(getValuesFromXPath(document, key, xPath));
        }

        var language = (isSingleChapter) ? "/html/body/div/div/div/div/dl/dd[@class='language']"
            :"/html/body/div/div/div/div/div/dl/dd[@class='language']";
        tags.addAll(getValuesFromXPath(document, AO3TagKey.LANGUAGE.name(), language));

        tag = (isSingleChapter) ? "/html/body/div/div/div/div/dl/dd/dl/dd[@class='${statGroup}']"
            : "/html/body/div/div/div/div/div/dl/dd/dl/dd[@class='${statGroup}']";

        var statGroups = Arrays.asList("comments", "kudos", "bookmarks", "hits");
        keys = Arrays.asList(AO3TagKey.COMMENTS.name(), AO3TagKey.FAVORITES.name(), AO3TagKey.HITS.name(), AO3TagKey.BOOKMARKS.name(),
                AO3TagKey.HITS.name());
        for(int i=0; i<statGroups.size(); i++ ){
            var group = statGroups.get(i);
            var key = keys.get(i);
            var xPath = tag.replace("${statGroup}", group);
            tags.addAll(getValuesFromXPath(document, key, xPath));

        }

        var published = (isSingleChapter) ? "/html/body/div/div/div/div/dl/dd/dl/dd[@class='published']"
            : "/html/body/div/div/div/div/div/dl/dd/dl/dd[@class='published']";
        book.setPublished(getDate(document, published));

        var updated = (isSingleChapter) ? "/html/body/div/div/div/div/dl/dd/dl/dd[@class='status']"
                : "/html/body/div/div/div/div/div/dl/dd/dl/dd[@class='status']";
        book.setUpdated(getDate(document, updated));

        book.setTags(tags);
    }

    private List<Tag> getValuesFromXPath(Document document, String key, String xPath){
        var elements = Xsoup.compile(xPath).evaluate(document).getElements();
        return elements.stream().map(element -> new Tag(key, element.text())).collect(Collectors.toList());
    }

    private Date getDate(Document document, String xPath){
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        var elements = Xsoup.compile(xPath).evaluate(document).getElements();
        var dateString = elements.stream().map(Element::text).findFirst().orElse("");
        try {
            return simpleDateFormat.parse(dateString);
        }catch (ParseException e){
            return null;
        }
    }

    private Chapter downloadChapter(String uri, boolean isSingleChapter) throws Exception{
        Chapter ret = new Chapter();

        URL test = new URL(uri);
        URLConnection connection = test.openConnection();
        String out = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
        Document document = Jsoup.parse(out);
        ret.setDescription(Xsoup.compile(CHAPTER_SUMMARY_XML).evaluate(document).getElements().text());
        ret.setContent(Xsoup.compile((isSingleChapter) ? TEXT_XML : TEXT_XML_CHAPTERED).evaluate(document).getElements().text());
        return ret;
    }
}
