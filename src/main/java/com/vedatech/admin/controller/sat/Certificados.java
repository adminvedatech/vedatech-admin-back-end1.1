package com.vedatech.admin.controller.sat;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.utils.Base64;
@Service
public class Certificados implements CertificadosIf{
    @Override
    public X509Certificate getCertificate(File cer) throws CertificateException, IOException {

        FileInputStream is = null;

        try {

            is = new FileInputStream(cer);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
           return  (X509Certificate) cf.generateCertificate(is);

        }finally {

            if(is !=null ){
                is.close();
            }
        }
    }

    @Override
    public String getCertificateBase64(X509Certificate certificate) throws CertificateEncodingException {
        return new String(Base64.encode( certificate.getEncoded()));
    }

    @Override
    public String getCertificateNumber(X509Certificate certificate) {
        byte[] byteArray = certificate.getSerialNumber().toByteArray();
        String value = new String(byteArray);
        return value;
    }
}
