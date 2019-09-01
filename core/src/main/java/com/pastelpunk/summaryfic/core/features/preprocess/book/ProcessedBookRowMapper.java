package com.pastelpunk.summaryfic.core.features.preprocess.book;

import com.datastax.driver.core.Row;
import com.pastelpunk.summaryfic.core.features.util.RowMapper;
import com.pastelpunk.summaryfic.core.models.processed.NGram;
import com.pastelpunk.summaryfic.core.models.processed.similarity.ProcessedBook;
import com.pastelpunk.summaryfic.core.models.raw.Tag;

public class ProcessedBookRowMapper implements RowMapper<ProcessedBook> {

    @Override
    public ProcessedBook map(Row row) {
        var ret = new ProcessedBook();
        ret.setId(row.getString("id"));
        ret.setCreated(row.getTimestamp("created"));
        ret.setModified(row.getTimestamp("modified"));
        ret.setDeleted(row.getBool("deleted"));
        ret.setIntakeJobId(row.getString("intakeJobId"));
        ret.setSource(row.getString("source"));
        ret.setUri(row.getString("uri"));
        ret.setTitle(row.getString("title"));
        ret.setAuthor(row.getString("author"));
        ret.setUpdated(row.getTimestamp("updated"));
        ret.setPublished(row.getTimestamp("published"));
        ret.setTags(row.getList("tags", Tag.class));
        ret.setUnigrams(row.getList("unigrams", NGram.class));

        return ret;
    }
}
