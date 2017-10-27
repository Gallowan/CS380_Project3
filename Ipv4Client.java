/**
 * CS 380.01 - Computer Networks
 * Professor: NDavarpanah
 *
 * Project 3
 * Ipv4Client
 *
 * Justin Galloway
 */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class Ipv4Client{
    public static void main(String[] args)throws IOException{
        try(Socket socket = new Socket("18.221.102.182", 38003)) {
            // Initialization
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            OutputStream os = socket.getOutputStream();
            BufferedReader br = new BufferedReader(isr);

            // Iterate through checks
            for(int i = 1; i < 13; i++){
                int data_size = (int)Math.pow(2.0, i);
                short data_length = (short)(data_size + 20);

                byte[] p = new byte[data_length];
                byte[] address = socket.getInetAddress().getAddress();

                // Initialization of the byte array into packets
                p[0] = 0b01000101;
                p[1] = 0;
                p[2] = (byte)((data_length & 0xFF00) >>> 8);
                p[3] = (byte)(data_length & 0x00FF);
                p[4] = 0;
                p[5] = 0;
                p[6] = (byte)0x40;
                p[7] = 0;
                p[8] = 50;
                p[9] = 6;
                p[10] = 0;
                p[11] = 0;

                for(int j = 0; j < 4; j++) {
                    p[j + 16] = address[j];
                }
                short check = checkSum(p);

                if(data_size == 128) {
                    check -= 256;
                }
                p[10] = (byte)((check & 0xFF00) >>> 8);
                p[11] = (byte)(check & 0x00FF);

                os.write(p);

                System.out.println("data length: " + data_size);
                String fromServer = br.readLine();
                System.out.println(fromServer);
                if (fromServer != "good:") {
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception caught.");
        }
    }

    public static short checkSum(byte[] p) {
        long sum = 0;
        int length = p.length;
        int i = 0;

        while(length >1){
            long highVal = ((p[i] << 8) & 0xFF00);
            long lowVal = ((p[i+1]) & 0x00FF);
            long value = highVal | lowVal;
            sum += value;

            if ((sum & 0xFFFF0000) > 0){
                sum = sum & 0xFFFF;
                sum += 1;
            }
            i += 2;
            length -= 2;
        }

        if(length > 0) {
            sum += (p[i] << 8 & 0xFF00);
            if ((sum & 0xFFFF0000) > 0) {
                sum &= 0x0000FFFF;
                sum++;
            }
        }

        sum = sum & 0xFFFF;
        return (short)~sum;
    }

//    public static short checkSum(byte[] p){
//        long sum = 0;
//        for(int i = 0; i < 20; i += 2){
//            int current = p[i] & 0xFF;
//            current <<= 8;
//            current |= p[i+1];
//            sum += current;
//
//            sum += (p[i] << 8 & 0xFF00);
//            if((sum & 0xFFFF0000)!=0){
//                sum &= 0x0000FFFF;
//                sum++;
//            }
//        }
//        sum = ~sum;
//        sum = sum & 0xFFFF;
//        return (short)sum;
//    }

}