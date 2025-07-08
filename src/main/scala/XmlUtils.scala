import java.io.File
import javax.xml.validation.SchemaFactory
import os.Path
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource

object XmlUtils:
  /** Validates a given xmf file using an xsd file using. Parsing errors are outputed to
    * output/{xmlFileName}-parsing-errors.txt
    *
    * @param xmlFile
    *   The xml file to validate
    * @param xsdFile
    *   The xsd file which is used to validate the entries in the xml file
    * @return
    *   True if no parsing errors; False otherwise
    */
  private def validate(xmlFile: File, xsdFile: File): Boolean =
    val errorsPath = os.pwd / "output" / s"${xmlFile.getName}-parsing-errors.txt"

    if os.exists(errorsPath) then
      os.remove(errorsPath)

    val schemaFactory =
      SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schema        = schemaFactory.newSchema(xsdFile)

    val validator = schema.newValidator()

    try
      validator.validate(new StreamSource(xmlFile))
      true
    catch
      case pe: org.xml.sax.SAXParseException =>
        os.write.append(
          errorsPath,
          s"SAXParseException at line: ${pe.getLineNumber()} in file: ${xmlFile.getName()}: ${pe.getMessage()}\n"
        )
        false
      case ex                                =>
        throw ex

  /**
    * Returns Xml filePath list for the given xml name.
    * Will only return paths that contain no parsing errors
    *
    * @param contains The xml file name
    * @param xmlFolder The folder that contains the xmls
    * @param xsdFile The xsd file that is used to validate the xml file
    * @return A list of xml file paths
    */
  def getXmlPaths(
      contains: String,
      xmlFolder: IndexedSeq[Path],
      xsdFile: File
  ): IndexedSeq[Path] =
    for
      path <- xmlFolder
      if path.ext == "xml" && path.last.contains(contains)
      if validate(new File(path.toString), xsdFile)
    yield (path)

  /**
    * Finds and returns a xsd file.
    * Throws an error if not found
    *
    * @param fileName the name of the xsd file to find
    * @param xsdFolder the folder where the xsds are
    * @return an xsd file of type File 
    */
  def findXsdFile(
      fileName: String,
      xsdFolder: IndexedSeq[Path]
  ): File =
    val filePath = xsdFolder.find(_.last == fileName + ".xsd") match {
      case None        => throw Error(s"$fileName xsd file not found")
      case Some(value) => value
    }

    File(filePath.toString)
