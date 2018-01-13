package org.dol.framework.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class SystemUtil {

    public static OSPlatform platform;
    public static String localHostIP = "";
    public static String localHostName = "";
    public static String mac = "";
    private static String OS;

    static {
        try {
            OS = System.getProperty("os.name").toLowerCase();
            initOSPlatform();
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            initLocalAddress();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static void initLocalAddress() {

        try {
            InetAddress address = Inet4Address.getLocalHost();
            if (address != null && !address.isLoopbackAddress() && !address.getHostAddress().equals("127.0.0.1") && !address.getHostAddress().contains(":")) {
                localHostIP = address.getHostAddress();
                localHostName = address.getHostName();
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
                mac = getMac(networkInterface);
                return;
            }
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = netInterfaces.nextElement();
                if (networkInterface.isLoopback()
                        || networkInterface.isVirtual()
                        || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    try {
                        if ((inetAddress instanceof Inet4Address)) {
                            String ip = inetAddress.getHostAddress();
                            if (ip.equals("127.0.0.1") || ip.contains(":")) {
                                continue;
                            }
                            localHostIP = ip;
                            localHostName = inetAddress.getHostName();
                            mac = getMac(networkInterface);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SocketException e) {

        }
    }

    private static String getMac(NetworkInterface networkInterface) throws SocketException {
        byte[] data = networkInterface.getHardwareAddress();
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : data) {
            int temp = b & 0xff;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                stringBuilder.append("0" + str + "-");
            } else {
                stringBuilder.append(str + "-");
            }
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1).toUpperCase();
    }

    public static void main(String[] args) {
        System.out.println(localHostIP);
        System.out.println(localHostName);
        System.out.println(mac);
    }

    public static String getLocalHostIP() {
        return localHostIP;
    }

    public static String getLocalHostName() {
        return localHostName;
    }

    public static boolean isLinux() {
        return OS.indexOf("linux") >= 0;
    }

    public static boolean isMacOS() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0;
    }

    public static boolean isMacOSX() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public static boolean isWindows() {
        return OS.indexOf("windows") >= 0;
    }

    public static boolean isOS2() {
        return OS.indexOf("os/2") >= 0;
    }

    public static boolean isSolaris() {
        return OS.indexOf("solaris") >= 0;
    }

    public static boolean isSunOS() {
        return OS.indexOf("sunos") >= 0;
    }

    public static boolean isMPEiX() {
        return OS.indexOf("mpe/ix") >= 0;
    }

    public static boolean isHPUX() {
        return OS.indexOf("hp-ux") >= 0;
    }

    public static boolean isAix() {
        return OS.indexOf("aix") >= 0;
    }

    public static boolean isOS390() {
        return OS.indexOf("os/390") >= 0;
    }

    public static boolean isFreeBSD() {
        return OS.indexOf("freebsd") >= 0;
    }

    public static boolean isIrix() {
        return OS.indexOf("irix") >= 0;
    }

    public static boolean isDigitalUnix() {
        return OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0;
    }

    public static boolean isNetWare() {
        return OS.indexOf("netware") >= 0;
    }

    public static boolean isOSF1() {
        return OS.indexOf("osf1") >= 0;
    }

    public static boolean isOpenVMS() {
        return OS.indexOf("openvms") >= 0;
    }

    /**
     * 获取操作系统名字
     *
     * @return 操作系统名
     */
    private static void initOSPlatform() {
        if (isAix()) {
            SystemUtil.platform = OSPlatform.AIX;
        } else if (isDigitalUnix()) {
            SystemUtil.platform = OSPlatform.Digital_Unix;
        } else if (isFreeBSD()) {
            SystemUtil.platform = OSPlatform.FreeBSD;
        } else if (isHPUX()) {
            SystemUtil.platform = OSPlatform.HP_UX;
        } else if (isIrix()) {
            SystemUtil.platform = OSPlatform.Irix;
        } else if (isLinux()) {
            SystemUtil.platform = OSPlatform.Linux;
        } else if (isMacOS()) {
            SystemUtil.platform = OSPlatform.Mac_OS;
        } else if (isMacOSX()) {
            SystemUtil.platform = OSPlatform.Mac_OS_X;
        } else if (isMPEiX()) {
            SystemUtil.platform = OSPlatform.MPEiX;
        } else if (isNetWare()) {
            SystemUtil.platform = OSPlatform.NetWare_411;
        } else if (isOpenVMS()) {
            SystemUtil.platform = OSPlatform.OpenVMS;
        } else if (isOS2()) {
            SystemUtil.platform = OSPlatform.OS2;
        } else if (isOS390()) {
            SystemUtil.platform = OSPlatform.OS390;
        } else if (isOSF1()) {
            SystemUtil.platform = OSPlatform.OSF1;
        } else if (isSolaris()) {
            SystemUtil.platform = OSPlatform.Solaris;
        } else if (isSunOS()) {
            SystemUtil.platform = OSPlatform.SunOS;
        } else if (isWindows()) {
            SystemUtil.platform = OSPlatform.Windows;
        } else {
            SystemUtil.platform = OSPlatform.Others;
        }
    }

}
