package xite.engine.model

import java.util.concurrent.atomic.AtomicLong

import shapeless.tag.@@

object Video {

  type Id = Long @@ Video

  object Id {

    private val nextId = new AtomicLong()

    def apply(in: Long): Video.Id = shapeless.tag[Video][Long](in)
    def apply(): Video.Id = Id(nextId.incrementAndGet())
  }

  def apply(): Video = Video(Id())
}

case class Video(id: Video.Id)
