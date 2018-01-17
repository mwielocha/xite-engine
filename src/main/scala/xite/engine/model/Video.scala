package xite.engine.model

import shapeless.tag.@@

object Video {

  type Id = Long @@ Video

  object Id {
    def apply(in: Long): Video.Id = shapeless.tag[Video][Long](in)
  }
}

case class Video(id: Video.Id, views: Long)
