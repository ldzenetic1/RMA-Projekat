package etf.ri.rma.newsfeedapp.data.database

import NewsEntity
import NewsTagCrossRef
import TagEntity
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NewsWithTags(
    @Embedded val news: NewsEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            NewsTagCrossRef::class,
            parentColumn = "newsId",
            entityColumn = "tagsId"
        )
    )
    val tags: List<TagEntity>
)