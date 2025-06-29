package conec;

import java.net.*;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtils {
    public static String getRealWiFiAddress() {
        try {
            NetworkInterface wifi = NetworkInterface.getByName("wlan2");
            if (wifi != null && wifi.isUp() && !wifi.isLoopback() && !wifi.isVirtual()) {
                for (InterfaceAddress addr : wifi.getInterfaceAddresses()) {
                    InetAddress inetAddr = addr.getAddress();
                    if (inetAddr instanceof Inet4Address && !inetAddr.isLoopbackAddress()) {
                        return inetAddr.getHostAddress(); // 👉 debería ser 192.168.1.172
                    }
                }
            } else {
                System.err.println("Interfaz 'wlan2' no está disponible o no está activa.");
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }
}