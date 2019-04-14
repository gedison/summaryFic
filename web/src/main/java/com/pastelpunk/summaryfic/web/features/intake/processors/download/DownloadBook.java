package com.pastelpunk.summaryfic.web.features.intake.processors.download;

import com.pastelpunk.summaryfic.core.models.AO3TagKey;
import com.pastelpunk.summaryfic.core.models.Book;
import com.pastelpunk.summaryfic.core.models.Chapter;
import com.pastelpunk.summaryfic.core.models.Tag;
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
public class DownloadBook extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadBook.class);

    private static final String TITLE_XML = "//h2[contains(@class, 'title heading')]";
    private static final String AUTHOR_XML = "//h3[contains(@class, 'byline heading')]";
    private static final String CHAPTER_SUMMARY_XML = "//blockquote[contains(@class, 'userstuff')]";
    private static final String CHAPTER_LIST_ID = "selected_id";
    private static final String TEXT_XML = "//div[@id='chapters']/div[@class='userstuff']";
    private static final String TEXT_XML_CHAPTERED = "//div[@id='chapters']/div/div[@class='userstuff module']/p";


    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed to download book {}", e);
    }

    protected void execute(Exchange exchange) throws Exception {
        RestExchange<String, Book> restExchange = new RestExchange<>(exchange);

        var intakeJobId = restExchange.get(IntakeConstants.JOB_ID).toString();
        var uri = restExchange.getInputObject();
        var url = "https://archiveofourown.org"+uri;

        Book book = downloadBook(url);
        book.setIntakeJobId(intakeJobId);

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

        setTags(document, ret);

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
                    chapters.add(downloadChapter(url , true));
                }
            }catch (Exception e){
                LOGGER.info("{}",e.getMessage(),e);
            }
        }

        int i = 0;
        for(Chapter chapter : chapters){
            chapter.setOrdering(i++);
        }

        ret.setChapters(chapters);
        return ret;
    }

    private void setTags(Document document, Book book){

        List<Tag> tags = new ArrayList<>();

        List<String> tagGroups = Arrays.asList("rating tags","warning tags","category tags","fandom tags",
                "relationship tags","character tags","freeform tags");
        List<String> keys = Arrays.asList(AO3TagKey.RATING.name(), AO3TagKey.ARCHIVE_WARNING.name(), AO3TagKey.CATEGORY.name(),
                AO3TagKey.FANDOM.name(), AO3TagKey.RELATIONSHIP.name(), AO3TagKey.CHARACTER.name(), AO3TagKey.ADDITIONAL_TAG.name());
        String tag = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='${tagGroup}']/ul/li/a";
        for(int i=0; i<tagGroups.size(); i++){
            var group = tagGroups.get(i);
            var key = keys.get(i);
            var xPath = tag.replace("${tagGroup}", group);
            tags.addAll(getValuesFromXPath(document, key, xPath));
        }


        String language = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='language']";
        tags.addAll(getValuesFromXPath(document, AO3TagKey.LANGUAGE.name(), language));
        String comments = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='comments']";
        tags.addAll(getValuesFromXPath(document, AO3TagKey.COMMENTS.name(), comments));
        String kudos = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='kudos']";
        tags.addAll(getValuesFromXPath(document, AO3TagKey.FAVORITES.name(), kudos));
        String hits = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='bookmarks']/a";
        tags.addAll(getValuesFromXPath(document, AO3TagKey.HITS.name(), hits));
        String bookmarks = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='hits']";
        tags.addAll(getValuesFromXPath(document, AO3TagKey.BOOKMARKS.name(), bookmarks));

        String published = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='published']";
        book.setPublished(getDate(document, published));
        String updated = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='status']";
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
            Date date = simpleDateFormat.parse(dateString);
            return date;
        }catch (ParseException e){
            return null;
        }
    }

    private Chapter downloadChapter(String uri, boolean isSingleChapter) throws Exception{
        Chapter ret = new Chapter();

        URL test = new URL(uri);
        URLConnection connection = test.openConnection();
        String out = new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next();
        Document document = Jsoup.parse(out);
        ret.setDescription(Xsoup.compile(CHAPTER_SUMMARY_XML).evaluate(document).getElements().text());
        ret.setContent(Xsoup.compile((isSingleChapter) ? TEXT_XML : TEXT_XML_CHAPTERED).evaluate(document).getElements().text());
        return ret;
    }
}
