package com.vedatech.admin.controller.sat;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface CertificadosIf {

    X509Certificate getCertificate(File cer) throws CertificateException, IOException;
    String getCertificateBase64 (X509Certificate certificate) throws CertificateEncodingException;
    String getCertificateNumber(X509Certificate certificate);
}
