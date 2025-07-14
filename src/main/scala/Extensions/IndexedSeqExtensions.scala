package Extensions

import os.Path
import java.io.File
import Utils.XmlUtils
import Utils.OutputUtils

object IndexedSeqExtensions {
  extension (indexedSeq: IndexedSeq[Path]) {
    def getXsdOrExit(
        fileName: String
    ): File =
      indexedSeq.find(_.last == fileName + ".xsd") match
        case None           =>
          OutputUtils.writeAppendErrors(
            "xsdFileNotFound.txt",
            s".xsd file with name '$fileName' not found"
          )
          sys.exit(1)
        case Some(filePath) => File(filePath.toString)

    def getXmlsOrExit(
        fileName: String,
        xsdFile: File
    ): IndexedSeq[File] =
      val searchResults = for
        path <- indexedSeq
        if path.ext == "xml" && path.last.startsWith(fileName)
      yield {
        XmlUtils.validate(new File(path.toString), xsdFile) match
          case None        => Right(File(path.toString))
          case Some(error) => Left(error)
      }

      val (errors, results) = searchResults.partitionMap(identity)

      if results.isEmpty then
        OutputUtils.writeErrors(
          s"$fileName-not-found.txt",
          s".xml files with name '$fileName' not found"
        )
        sys.exit(1)
      else
        if errors.nonEmpty then
          OutputUtils.writeErrors(s"$fileName-parsing-errors.txt", errors.mkString("\n"))

        results

  }
}
