package com.vedatech.admin.controller.sat;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.vedatech.admin.model.bank.BankTransaction;
import com.vedatech.admin.model.invoice.Comprobante;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SelloDigitalImpl implements SelloDigitalIF {

    @Override
    public String convertXmlToString(Comprobante comprobante) throws PropertyException {

        String comprobanteXml = "";
        try {

        JAXBContext  context = JAXBContext.newInstance(Comprobante.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(comprobante, writer);
        System.out.println("XML FILE " + writer.toString());
        comprobanteXml = writer.toString();
        }catch (JAXBException e) {

            e.printStackTrace();
        }

        return comprobanteXml;
    }

    @Override
    public String generarCadenaOriginale(String comprobanteXml) throws TransformerException {

        // cargar el archivo XSLT
        File xslt = new File("C:/SAT/cadenaoriginal_3_3.xslt");
        StreamSource sourceXSL = new StreamSource(xslt);

        // crear el procesador XSLT que nos ayudará a generar la cadena original
        // con base en las reglas del archivo XSLT
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(sourceXSL);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        transformer.transform(new StreamSource(new StringReader(comprobanteXml)), new StreamResult(outputStream));

        String result = "";

        try {

            result = outputStream.toString("UTF-8");

        } catch(UnsupportedEncodingException e) {
            Logger.getLogger(BankTransaction.class.getName()).log(Level.SEVERE, null, e);

        }
        return result;
    }

    @Override
    public PrivateKey getPrivateKey(File keyFile, String pass) throws GeneralSecurityException, IOException {

        FileInputStream inputStream = new FileInputStream(keyFile);
         org.apache.commons.ssl.PKCS8Key pkcs8Key = new org.apache.commons.ssl.PKCS8Key(inputStream, pass.toCharArray());
         byte[] decrypted = pkcs8Key.getDecryptedBytes();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decrypted);
        PrivateKey pk = null;

        if(pkcs8Key.isDSA()){
            pk = KeyFactory.getInstance("DSA").generatePrivate(spec);
        }else if (pkcs8Key.isRSA()){
                pk = KeyFactory.getInstance("RSA").generatePrivate(spec);
            }
        pk = pkcs8Key.getPrivateKey();

        return pk;

    }

    @Override
    public String generarSelloDigital(PrivateKey key, String cadenaoriginal) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {

        Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(key, new SecureRandom());

            try {
                signature.update(cadenaoriginal.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(BankTransaction.class.getName()).log(Level.SEVERE, null, ex);
            }

            byte[] sign = signature.sign();

        return new String(Base64.encode(sign));
    }
}
