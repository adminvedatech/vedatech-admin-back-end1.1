package com.vedatech.admin.controller;


import com.vedatech.admin.controller.sat.CertificadosIf;
import com.vedatech.admin.model.bank.BankTransaction;
import com.vedatech.admin.model.invoice.CMetodoPago;
import com.vedatech.admin.model.invoice.CMoneda;
import com.vedatech.admin.model.invoice.CTipoDeComprobante;
import com.vedatech.admin.model.invoice.Comprobante;
import com.vedatech.admin.service.bank.BankTransactionService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import sun.security.pkcs.PKCS8Key;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/bank")
public class BankTransactionController {

    private static String UPLOADED_FOLDER = "C://SAT2//";

    @Autowired
    BankTransactionService bankTransactionService;

    @Autowired
    CertificadosIf certificadosIf;

    //-------------------Create a Transaction--------------------------------------------------------

    @PostMapping(value = "/add-bank-transaction")
    public ResponseEntity<String> createBankTransaction(@RequestBody BankTransaction transactions, UriComponentsBuilder ucBuilder) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        HttpHeaders headers = new HttpHeaders();

        if (findBankTransaction(transactions) != null) {
            headers.set("error", "la referencia ya existe");
            return new ResponseEntity<String>(headers, HttpStatus.CONFLICT);
        }
        return saveBankTransaction(transactions);

    }

    //-------------------Get Transactions between Dates and Bank Id--------------------------------------------------------

    @GetMapping(value = "/get-bank-transaction/{after}/{before}/{id}")
    public ResponseEntity<List<BankTransaction>> readBankTransactions(@PathVariable("after") String after, @PathVariable("before") String before, @PathVariable("id") Long id) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        HttpHeaders headers = new HttpHeaders();

        try {

            Date date1 = formatter.parse(after);
            Date date2 = formatter.parse(before);
            List<BankTransaction> bankTransactions = bankTransactionService.findBankTransactionByDateGreaterThanEqualAndDateLessThanEqualAndBank_Id(date1, date2, id);

            return new ResponseEntity<List<BankTransaction>>(bankTransactions, headers, HttpStatus.OK);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<List<BankTransaction>>(headers, HttpStatus.CONFLICT);
    }


    //-------------------update a Bank Transaction--------------------------------------------------------

    @RequestMapping(value = "/update-bank-transaction", method = RequestMethod.PUT)
    public ResponseEntity<String> updateBankTransaction(@RequestBody BankTransaction transactions, UriComponentsBuilder ucBuilder) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        HttpHeaders headers = new HttpHeaders();
        //    BankTransaction updateTransaction = bankTransactionService.findBankTransactionByIdAndBank_Id(transactions.getId(), transactions.getBank().getId());
        System.out.println(transactions.toString());
        return saveBankTransaction(transactions);
    }


    //-------------------Delete Bank Transaction--------------------------------------------------------

    @DeleteMapping(value = "/delete-bank-transaction")
    public ResponseEntity<String> deleteBankTransaction(@RequestBody BankTransaction transaction) {
        HttpHeaders headers = new HttpHeaders();

        try {

            BankTransaction bankTransaction = findBankTransaction(transaction);
            if (bankTransaction != null) {
                bankTransactionService.deleteBankTransaction(bankTransaction);
                headers.set("success", "la transaccion se a borrado con exito");
                return new ResponseEntity<String>(headers, HttpStatus.OK);

            } else {
                headers.set("error", "no existe el archivo solicitado");
                return new ResponseEntity<String>(headers, HttpStatus.CONFLICT);

            }

        } catch (Error e) {
            headers.set("error", "error al conectar a la base de datos");
            return new ResponseEntity<String>(headers, HttpStatus.CONFLICT);
        }
    }


    //--------------------Search for Bank Transaction by reference and idBank------------------

    BankTransaction findBankTransaction(BankTransaction bankTransaction) {
        return bankTransactionService.findByReferenceAndByBankId(bankTransaction.getReference(), bankTransaction.getBank().getId());
    }


    //---------------------------Save Bank Transaction ---------------------------------

    public ResponseEntity<String> saveBankTransaction(BankTransaction bankTransaction) {

        HttpHeaders headers = new HttpHeaders();
        try {

            bankTransactionService.save(bankTransaction);
            headers.set("success", "transaction grabada con exito");
            return new ResponseEntity<String>(headers, HttpStatus.CREATED);

        } catch (Error e) {

            headers.set("error", "error al gragar datos");
            return new ResponseEntity<String>(headers, HttpStatus.CONFLICT);

        }
    }


    //-------------------Received Xml File--------------------------------------------------------


    @RequestMapping(value = "/send-xml-file", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getXmlInvoice(@RequestBody String comprobante) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        //Save the uploaded file to this folder
        System.out.println("Comprobante " + comprobante.toString());
        StringReader com = new StringReader(comprobante);
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(Comprobante.class);
//            File cfdi = new File("C:/SAT2/ANT021004RI7_PUHS6505319L9_764.xml");

//            System.out.println("CFDI " + cfdim);

            Unmarshaller unmarshaller = context.createUnmarshaller();

            Comprobante unmarshal = (Comprobante) unmarshaller.unmarshal(com);
            System.out.println(unmarshal.getEmisor().getNombre());
            System.out.println(unmarshal.getReceptor().getNombre());
            System.out.println(unmarshal.getSubTotal());
            System.out.println(unmarshal.getTotal());


//            Create XML FILE

//          Obtener Certificado y la Key

            File fileCer = new File("C:/SAT2/CSDAAA010101AAA/CSD01_AAA010101AAA.cer");

            File fileKey = new File("C:/SAT2/CSDAAA010101AAA/CSD01_AAA010101AAA.key");

            X509Certificate certificate = certificadosIf.getCertificate(fileCer);
            String certificado = certificadosIf.getCertificateBase64(certificate);
            String certificadoNo = certificadosIf.getCertificateNumber(certificate);

            System.out.println("CERTIFICADO " + certificado);
            System.out.println("CERTIFICADO Number" + certificadoNo);



//            InputStream is = new FileInputStream(fileCer);
//            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            X509Certificate certificado = (X509Certificate) cf.generateCertificate(is);
//            certificado.getSerialNumber();
//            byte[] byteArray = certificado.getSerialNumber().toByteArray();
//            String value = new String(byteArray);
//            System.out.println("CERTIFICADO " + value);

            String password = "12345678a";

//            Leer Key

            // read key bytes
            File in = new File("C:/SAT2/CSDAAA010101AAA/CSD01_AAA010101AAA.key");
//            byte[] keyBytes = new byte[in.available()];
//            in.read(keyBytes);
//            in.close();
            String passphrase = "12345678a";
//            byte[] keyBytes = FileUtils.readFileToByteArray(pkeyFile);
//            getPrivateKey(in, passphrase);


//            Leer Certificado

            Marshaller marshaller = context.createMarshaller();

            Comprobante comprobante1 = new Comprobante();
            comprobante1.setVersion("3.3");
            comprobante1.setSerie("CFDI");
            comprobante1.setFolio("455545");
            comprobante1.setNoCertificado(certificadoNo);
            comprobante1.setCertificado(certificado);
//            comprobante1.setFecha(); pendiente
//            comprobante1.setSello(); pendiente
            comprobante1.setFormaPago("99");
            comprobante1.setFormaPago("PPD");
//            comprobante1.setCertificado(); pendiente
            comprobante1.setSubTotal(BigDecimal.valueOf(1000.25));
            comprobante1.setMoneda(CMoneda.MXN);
            comprobante1.setTotal(BigDecimal.valueOf(1000.25));
            comprobante1.setTipoDeComprobante(CTipoDeComprobante.I);
            comprobante1.setMetodoPago(CMetodoPago.PUE);
            comprobante1.setLugarExpedicion("some value from bd");
            Comprobante.Emisor emisor = new Comprobante.Emisor();
            emisor.setNombre("Alimentos Naturales de Trigo Germinado S.A.");
            emisor.setRfc("ANT021004RI7");
            emisor.setRegimenFiscal("Regimen Contribuyente");
            comprobante1.setEmisor(emisor);

            Comprobante.Receptor receptor = new Comprobante.Receptor();
            receptor.setNombre("Cia General de Viveres S.A.");
            receptor.setRfc("CGV021105");
            receptor.setNumRegIdTrib("some value from bd");
            Comprobante.Conceptos conceptosList = new Comprobante.Conceptos();
            Comprobante.Conceptos.Concepto concepto = new Comprobante.Conceptos.Concepto();
            concepto.setClaveProdServ("from bd");
            concepto.setNoIdentificacion("from bd");
            concepto.setNoIdentificacion("form bd");
            concepto.setCantidad(BigDecimal.valueOf(100));
            concepto.setClaveUnidad("from bd");
            concepto.setUnidad("pza from bd");
            concepto.setDescripcion("TOSTADA DE TRIGO GERMINDADO");
            concepto.setValorUnitario(BigDecimal.valueOf(22));
            concepto.setImporte(BigDecimal.valueOf(3000));
            conceptosList.getConcepto().add(concepto);
            comprobante1.setReceptor(receptor);
            comprobante1.setConceptos(conceptosList);

            StringWriter writer = new StringWriter();
            marshaller.marshal(comprobante1, writer);
            System.out.println("XML FILE " + writer.toString());


        } catch (JAXBException | CertificateException e) {
            e.printStackTrace();
        }


        headers.set("toodo ok ", "suxxess");


        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    public static void muestraContenido(String archivo) throws FileNotFoundException, IOException {
        String cadena;
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        while ((cadena = b.readLine()) != null) {
            System.out.println(cadena);
        }
        b.close();
    }

    /**
     * Retorna una llave privada utilizando los datos y la passphrase indicada. Se utiliza not-yet-commons-ssl para esto
     * ya que no hay manera simple de hacerlo directo con java
     */
//    private byte[] extractProtectedPrivateKey(File privateKeyInputStream, String keyPassword) {
//        byte[] bytes;
//
//        try {
//            if(keyPassword == null) {
//                bytes = privateKeyInputStream.toByteArray(privateKeyInputStream);
//            } else {
//                bytes = new PKCS8Key(privateKeyInputStream, keyPassword.toCharArray()).getDecryptedBytes();
//            }
//        } catch (GeneralSecurityException e) {
//            throw new KeyException("La contraseña del certificado no es correcta", e.getCause());
//        } catch (IOException ioe){
//            throw new KeyException(ioe.getMessage(), ioe.getCause());
//        }
//
//        return bytes;
//    }


}

