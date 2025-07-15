package Utils

import java.io.File
import javax.xml.validation.SchemaFactory
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource

object XmlUtils:
  /** Validates a given xml file using an xsd file.
    *
    * Parsing errors are outputed to output/{xmlFileName}-parsing-errors.txt
    *
    * @param xmlFile
    *   The xml file to validate
    * @param xsdFile
    *   The xsd file which is used to validate the entries in the xml file
    * @return
    *   Error string if there was an error; None otherwise
    */
  def validate(xmlFile: File, xsdFile: File): Option[String] =
    val schemaFactory =
      SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schema        = schemaFactory.newSchema(xsdFile)

    val validator = schema.newValidator()

    try
      validator.validate(new StreamSource(xmlFile))
      None
    catch
      case pe: org.xml.sax.SAXParseException =>
        Some(
          s"SAXParseException at line: ${pe.getLineNumber()} in file: ${xmlFile.getName()}: ${pe.getMessage()}\n"
        )
      case otherException                    =>
        Some(
          s"Exception: ${otherException.getMessage()}: ${otherException.getStackTrace()}\n"
        )
