package mx.solucionesonline.num;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TCP {
    private final int MAXRECBYTES = 1024;

    public  static final int TCP_NO_ERROR = 0;
    public  static final int TCP_ERROR_UKNHOST = 1;
    public  static final int TCP_ERROR_CONNREFUSSED = 2;
    public  static final int TCP_ERROR_UNREACH = 3;
    public  static final int TCP_ERROR_TIMEDOUT = 4;
    public  static final int TCP_ERROR_UNKNOWN = 5;
    public  static final int TCP_ERROR_INVALID_ARGUMENT=6;
    public static final int TCP_ERROR_EPIPE=6;
    public static final short TCP_ERROR_RESPONSE=7;
    public  static final int TCP_ERROR_TIMEDOUT_DESCONNECT = 8;


    private String serverIP;
    private int serverPort;
    private String strError;
    private short nError;
    private String mResponse;
    private int mTimeOut, mSoTimeOut;
    private Socket mSocket;



    /**
     * Constuctor donde se inicializan las propiedades del objeto
     * @param ip    ip a la cual se va a conectar
     * @param port  Puerto del servidor
     */
    public TCP(String ip, int port) {
        this.serverIP = ip;
        this.serverPort = port;
        this.nError = TCP_NO_ERROR;
        this.mResponse = null;
        this.mTimeOut = 10000;
        mSoTimeOut = 10000;
    }

    /**
     * Modifica el timeout para obtener respuesta
     * @param ms
     */
    public void setTimeOut(int ms) {
        this.mTimeOut = ms;
    }
    public void setSoTimeOut(int ms){
        mSoTimeOut = ms;
        if (mSocket != null){
            try {
                mSocket.setSoTimeout(ms);
            } catch (SocketException e) {
            }
        }
    }

    /**
     * Permite la transmisión - recepción de información a través del mSocket de forma
     * cruda ya preprocesado. El mSocket ya debe estar creado
     * @param data
     * @return
     */
    private byte[] rawTransceive(byte[] data) {
        byte[] finalBytesReceived = null;
        byte[] bytesReceived = new byte[MAXRECBYTES];
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream =  null;

        this.nError = TCP_NO_ERROR;
        mResponse = "rawTransceive";
        try {
            dataOutputStream = new DataOutputStream(mSocket.getOutputStream());
            dataInputStream = new DataInputStream(mSocket.getInputStream());

            dataOutputStream.write(data);
            dataOutputStream.flush();
            int nBytes = dataInputStream.read(bytesReceived);

            if (nBytes > 0) {
                finalBytesReceived = new byte[nBytes];
                System.arraycopy(bytesReceived, 0, finalBytesReceived, 0, nBytes);
            }

        } catch (UnknownHostException e) {

            this.nError = TCP_ERROR_UKNHOST;
            mResponse = strError = e.toString();
            e.printStackTrace();

        } catch (IOException e) {

            mResponse = strError = e.toString();

            if (strError.contains("ECONNREFUSED") || strError.contains("refused")) {
                mResponse = strError = "** SecureTCP - Connection refused";
                this.nError = TCP_ERROR_CONNREFUSSED;
            } else if (strError.contains("ENETUNREACH") || strError.contains("unreach")) {
                mResponse = strError = "** SecureTCP - Network unreachable";
                this.nError = TCP_ERROR_UNREACH;
            } else if (strError.contains("Timeout")) {
                mResponse = strError = "** SecureTCP - TimeOut";
                this.nError = TCP_ERROR_TIMEDOUT;
            } else if(strError.contains("EINVAL")) {
                mResponse = strError = "** SecureTCP - Invalid argument";
                this.nError = TCP_ERROR_INVALID_ARGUMENT;
            } else if (strError.contains("EPIPE")) {
                mResponse = strError = "** SecureTCP - EPIPE";
                this.nError = TCP_ERROR_EPIPE;
            } else if (strError.contains("EBADF")) {
                mResponse = strError = "** SecureTCP - EBADF";
                this.nError = TCP_ERROR_EPIPE;
            } else if (strError.contains("ETIMEDOUT")) {
                mResponse = strError = "** TCP - ETIMEDOUT";
                this.nError = TCP_ERROR_TIMEDOUT_DESCONNECT;
            }  else {
                this.nError = TCP_ERROR_UNKNOWN;
            }

            e.printStackTrace();

        } catch (Exception e) {
            mResponse = strError = e.toString();
            this.nError = TCP_ERROR_UNKNOWN;
            e.printStackTrace();
        }

        if (this.nError != TCP_NO_ERROR) {
            // close current socket
            stopSocket();
            if (this.serverIP == Configuracion.ip) {
                Singleton.getInstance().ip = this.serverIP = Configuracion.ip;
                Singleton.getInstance().port = Configuracion.port;
            }
        }

        return finalBytesReceived;
    }

    public static String bytesToStringUTF(byte[] bytes) {
        if (bytes == null ) {
            return "";
        }
        char[] buffer = new char[bytes.length];
        for(int i = 0; i < buffer.length; i++) {
            char c = (char) (bytes[i] & 0x00FF);
            buffer[i] = c;
        }
        return new String(buffer);
    }

    private short sendAndReceiveString(String data) {
        try {
            byte[] tmpBytes = new byte[data.length()];
            char[] charArray = data.toCharArray();
            int index = 0;
            for (char d:charArray) {
                tmpBytes[index] = (byte) d;
                index++;
            }

            // enviamos tmpBytes
            byte[] res = rawTransceive(tmpBytes);

            if (res != null) {

                int nBytes = res.length;
                mResponse = bytesToStringUTF(res);

                if (nBytes > 0) {
                    mResponse = mResponse.substring(0, nBytes);
                }
                short tipoPaquete = (short) (mResponse.substring(0, 1).toCharArray()[0] - 0x20);
                if (tipoPaquete == -32) {
                    stopSocket();
                    mResponse = "** SecureTCP - RESPONSE";
                    nError = TCP_ERROR_RESPONSE;
                }
            } else {
                mResponse = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.nError;
    }


    /**
     * Se envía la cadena y se espera la respuesta
     * @param data  cadena de datos codificados a enviar
     * @return
     */
    public short sendAndReceive(String data) {

        try {
            if (mSocket == null || !mSocket.isConnected()) {
                mSocket = new Socket();
                mSocket.connect(new InetSocketAddress(serverIP, serverPort), mTimeOut);
                mSocket.setKeepAlive(true);
                mSocket.setSoTimeout(mSoTimeOut);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Encriptar información con llave de sesión


        // enviar y recibir informacion
        sendAndReceiveString(data);


        // Desencriptar información con llave de sesión

        // Guardarla en mresponse


        return this.nError;
    }

    public void stopSocket() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                ////Log.e("SecureTCP","could not close");
            }
            mSocket = null;
        }
    }

    /**
     * Devuelve la respuesta obtenida del servidor
     * @return Respuesta
     */
    public final String getResponse() {
        return this.mResponse;
    }

}
