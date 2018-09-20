package com.vedatech.admin.controller.sat;

import com.vedatech.admin.model.invoice.Comprobante;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;

public interface SelloDigitalIF {

    String convertXmlToString(Comprobante comprobante) throws PropertyException;
    String generarCadenaOriginale (String comprobanteXml) throws TransformerException;
    PrivateKey getPrivateKey(File key, String pass) throws GeneralSecurityException, FileNotFoundException, IOException;
    String generarSelloDigital(PrivateKey key, String cadenaoriginal) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException;

}
