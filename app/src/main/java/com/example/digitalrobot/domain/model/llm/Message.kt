package com.example.digitalrobot.domain.model.llm

import com.example.digitalrobot.domain.model.llm.common.Attachment
import com.example.digitalrobot.domain.model.llm.common.IncompleteDetails

data class Message(
    val id: String,
    val `object`: String,
    val created_at: Int,
    val thread_id: String,
    val status: String,
    val incomplete_details: IncompleteDetails? = null,
    val completed_at: Int? = null,
    val incompleted_at: Int? = null,
    val role: String,
    val content: List<Any>,
    val assistant_id: String? = null,
    val run_id: String? = null,
    val attachments: List<Attachment>? = null,
    val metadata: Any
)

data class MessageList(
    val `object`: String,
    val data: List<Message>
)

sealed class MessageContent {

    data class ImageFile(
        val type: String,
        val image_file: ImageFileObj
    ): MessageContent() {
        data class ImageFileObj(
            val file_id: String,
            val detail: String
        )
    }

    data class ImageUrl(
        val type: String,
        val image_url: ImageUrlObj
    ): MessageContent() {
        data class ImageUrlObj(
            val url: String,
            val detail: String
        )
    }

    data class Text(
        val type: String,
        val text: TextObj
    ): MessageContent() {
        data class TextObj(
            val value: String,
            val annotations: List<Any>
        )
        data class FileCitation(
            val type: String,
            val text: String,
            val file_citation: FileCitationObj,
            val start_index: Int,
            val end_index: Int
        )
        data class FileCitationObj(
            val file_id: String
        )
        data class FilePath(
            val type: String,
            val text: String,
            val file_path: FilePathObj,
            val start_index: Int,
            val end_index: Int
        )
        data class FilePathObj(
            val file_id: String
        )
    }

    data class Refusal(
        val type: String,
        val refusal: String
    ): MessageContent()

}
