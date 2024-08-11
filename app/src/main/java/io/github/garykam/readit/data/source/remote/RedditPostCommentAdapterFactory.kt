package io.github.garykam.readit.data.source.remote

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class RedditPostCommentAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val delegate: TypeAdapter<T> = gson.getDelegateAdapter(this, type)
        val elementAdapter = gson.getAdapter(JsonElement::class.java)

        return object : TypeAdapter<T>() {
            override fun write(writer: JsonWriter, value: T) {
                delegate.write(writer, value)
            }

            override fun read(reader: JsonReader): T {
                val jsonElement = elementAdapter.read(reader)
                if (jsonElement.isJsonObject) {
                    val jsonObject = jsonElement as JsonObject
                    if (jsonObject.has("replies") && jsonObject.get("replies").isJsonPrimitive) {
                        jsonElement.remove("replies")
                    }
                }

                return delegate.fromJsonTree(jsonElement)
            }
        }
    }
}
