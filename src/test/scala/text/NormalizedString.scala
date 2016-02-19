package text

import java.text.{Normalizer => JavaNormalizer}

/**
  * @author ynupc
  *         Created on 15/10/28
  */
object NormalizedString {
  def apply(str: StringOption): NormalizedString = {
    new NormalizedString(str)
  }
}

class NormalizedString(str: StringOption) extends AnyRef {
  private val normalizedString: StringOption = normalize

  private def normalize: StringOption = {
    Normalizer.normalize(str)
  }

  override def toString: String = {
    normalizedString.getOrElse("")
  }

  def toStringOption: StringOption = {
    normalizedString
  }

  override def hashCode: Int = {
    this.toString.toInt
  }

  override def equals(other: Any): Boolean = other match {
    case that: NormalizedString =>
      this.toString == that.toString
    case otherwise => false
  }

  def concat(nStr: NormalizedString): NormalizedString = {
    new NormalizedString(normalizedString.map(_.concat(nStr.toString)))
  }

  def split(regex: String): Array[NormalizedString] = {
    normalizedString match {
      case StringSome(nStr) =>
        nStr.split(regex) map {
          token =>
            new NormalizedString(StringOption(token))
        }
      case StringNone =>
        Array[NormalizedString]()
    }
  }

  def split(separator: Char): Array[NormalizedString] = {
    Normalizer.normalize(StringOption(separator.toString)) match {
      case StringSome(nSeparator) =>
        normalizedString match {
          case StringSome(nStr) =>
            nStr.split(nSeparator.head) map {
              token =>
                new NormalizedString(StringOption(token))
            }
          case StringNone =>
            Array[NormalizedString]()
        }
      case StringNone =>
        Array[NormalizedString]()
    }
  }

  def trim: NormalizedString = {
    normalizedString.map(_.trim)
    this
  }
}

object Normalizer {
  def normalize(str: StringOption): StringOption = {

    def normalizeCharacters(string: String): StringOption = {
      StringOption(
        JavaNormalizer.normalize(string.
          //波線記号の統一
          replaceAll("\u301C", "\uFF5E").//〜 ～
          replaceAll("\u007E", "\uFF5E"),//~ ～,
          JavaNormalizer.Form.NFKC).
          //イコール記号の削除
          replaceAll("\u003D", "").//=
          replaceAll("\uFF1D", "").//＝
          //中黒の削除
          replaceAll("\u30FB", "")//・
      )
    }

    str match {
      case StringSome(s) =>
        WordExpressionNormalizer.normalize(
          normalizeCharacters(s)
        )
      case StringNone =>
        StringNone
    }
  }
}

class NormalizedStringBuilder {
  val builder = new StringBuilder

  def append(nStr: NormalizedString): NormalizedStringBuilder = {
    builder.append(nStr.toString)
    this
  }

  def result(): NormalizedString = {
    new NormalizedString(StringOption(builder.toString()))
  }

  def clear(): Unit = {
    builder.clear()
  }
}

class NormalizedStringBuffer {
  val buffer = new StringBuffer

  def append(nStr: NormalizedString): NormalizedStringBuffer = {
    buffer.append(nStr.toString)
    this
  }

  def result(): NormalizedString = {
    new NormalizedString(StringOption(buffer.toString))
  }

  def clear(): Unit = {
    buffer.setLength(0)
  }
}