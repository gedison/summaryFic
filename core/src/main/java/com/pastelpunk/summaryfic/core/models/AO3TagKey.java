package com.pastelpunk.summaryfic.core.models;

import java.util.Arrays;
import java.util.List;

public enum AO3TagKey {
    CATEGORY,
    FANDOM,
    RELATIONSHIP,
    CHARACTER,
    ARCHIVE_WARNING,
    ADDITIONAL_TAG,
    RATING,
    FAVORITES,
    HITS,
    BOOKMARKS,
    COMMENTS,
    LANGUAGE;

    List<String> tagGroups = Arrays.asList("rating tags","warning tags","category tags","fandom tags",
            "relationship tags","character tags","freeform tags");

    String tags = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='${tagGroup}']/ul/li/a";
    String language = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='language']";
    String published = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='published']";
    String updated = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='status']";
    String comments = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='comments']";
    String kudos = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='kudos']";
    String hits = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='bookmarks']/a";
    String bookmarks = "/html/body/div/div/div[@id='main']/div[@class='work']/div[@class='wrapper']/dl[@class='work meta group']/dd[@class='stats']/dl[@class='stats']/dd[@class='hits']";

}
