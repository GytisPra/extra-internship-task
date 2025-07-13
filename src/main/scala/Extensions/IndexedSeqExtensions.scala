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
      val results = for
        path <- indexedSeq
        if path.ext == "xml" && path.last.startsWith(fileName)
        if XmlUtils.validate(new File(path.toString), xsdFile)
      yield (File(path.toString))

      if results.isEmpty then
        OutputUtils.writeAppendErrors(
          "xmlFilesNotFound.txt",
          s".xml files with name '$fileName' not found"
        )
        sys.exit(1)
      else results
  }
}
