package Utils

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
  def validate(xmlFile: File, xsdFile: File): Boolean =
    val schemaFactory =
      SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schema        = schemaFactory.newSchema(xsdFile)

    val validator = schema.newValidator()

    try
      validator.validate(new StreamSource(xmlFile))
      true
    catch
      case pe: org.xml.sax.SAXParseException =>
        val errorMsg =
          s"SAXParseException at line: ${pe.getLineNumber()} in file: ${xmlFile.getName()}: ${pe.getMessage()}\n"
        OutputUtils.writeErrors(s"${xmlFile.getName}-parsing-errors.txt", errorMsg)
        false
      case otherExeption                     =>
        throw otherExeption
