package xite.engine.utils

object StringHelpers {

  private val emailRegex =
    """^(?!\.)("([^"\r\\]|\\["\r\\])*"|([-a-zA-Z0-9!#$%&'*+/=?^_`{|}~]|(?<!\.)\.)*)(?<!\.)@[a-zA-Z0-9][\w\.-]*[a-zA-Z0-9]\.[a-zA-Z][a-zA-Z\.]*[a-zA-Z]$""".r

  def isValidEmail(in: String): Boolean = {
    (for {
      nonEmpty <- Option(in).filterNot(_.isEmpty)
      _ <- emailRegex.findFirstMatchIn(nonEmpty)
    } yield ()).isDefined
  }
}
