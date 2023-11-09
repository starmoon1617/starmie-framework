/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility Class for IP
 * 
 * @date 2023-10-13
 * @author Nathan Liao
 */
public class IpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpUtils.class);
    /**
     * Local Host
     */
    public static final String LOCALHOST = "127.0.0.1";

    /**
     * Any Host
     */
    public static final String ANYHOST = "0.0.0.0";

    /**
     * Local Address
     */
    private static InetAddress LOCALADDRESS = null;

    /**
     * 是否初始化
     */
    private static AtomicBoolean inited = new AtomicBoolean(false);;

    static {
        initLocalAddress();
    }

    private IpUtils() {

    }

    /**
     * get Local Host
     * 
     * @return
     */
    public static final String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? LOCALHOST : address.getHostAddress();
    }

    /**
     * get LocalAddress
     * 
     * @return
     */
    public static InetAddress getLocalAddress() {
        if (LOCALADDRESS == null) {
            inited.compareAndSet(true, false);
            initLocalAddress();
        }
        return LOCALADDRESS;
    }

    /**
     * get InetAddress
     * 
     * @param networkInterface
     * @return
     */
    private static InetAddress getInetAddress(NetworkInterface networkInterface) {
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        if (addresses == null) {
            return null;
        }
        InetAddress inetAddress = null;
        while (addresses.hasMoreElements()) {
            try {
                InetAddress address = addresses.nextElement();
                if (address.isLoopbackAddress()) {
                    continue;
                }
                if (address.isLinkLocalAddress()) {
                    continue;
                }
                if (!address.isSiteLocalAddress()) {
                    continue;
                }
                inetAddress = address;
                break;
            } catch (Throwable e) {
                LOGGER.warn("Failed to retriving InetAddress:", e);
            }
        }
        return inetAddress;
    }

    /**
     * initialize LocalAddress
     */
    private static synchronized void initLocalAddress() {
        synchronized (inited) {
            if (!inited.get()) {
                try {
                    InetAddress candidateAddress = InetAddress.getLocalHost();
                    String candidateHost = candidateAddress.getHostAddress();
                    InetAddress bestAddress = null;
                    InetAddress virtualAddress = null;
                    InetAddress vpnAddress = null;

                    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                    if (interfaces == null) {
                        LOCALADDRESS = candidateAddress;
                    } else {
                        while (interfaces.hasMoreElements()) {
                            NetworkInterface network = interfaces.nextElement();
                            if (!network.isUp() || network.isLoopback() || network.isVirtual()) {
                                continue;
                            }
                            boolean isVirtual = network.getDisplayName().contains("Virtual");
                            boolean isVpn = network.getDisplayName().contains("VPN");
                            // get virtualAddress
                            if (isVirtual) {
                                if (virtualAddress != null) {
                                    continue;
                                }
                                virtualAddress = getInetAddress(network);
                                continue;
                            }
                            // get vpnAddress
                            if (isVpn) {
                                if (vpnAddress != null) {
                                    continue;
                                }
                                vpnAddress = getInetAddress(network);
                                continue;
                            }
                            // bestAddress
                            bestAddress = getInetAddress(network);
                            if (bestAddress != null) {
                                LOCALADDRESS = bestAddress;
                                break;
                            }
                        }

                        if (bestAddress == null) {
                            // check if candidateAddress is 0.0.0.0/127.0.0.1
                            if (ANYHOST.equals(candidateHost) || LOCALHOST.equals(candidateHost)) {
                                if (vpnAddress != null) {
                                    LOCALADDRESS = vpnAddress;
                                } else if (virtualAddress != null) {
                                    LOCALADDRESS = virtualAddress;
                                }
                            }
                            if (LOCALADDRESS == null) {
                                LOCALADDRESS = candidateAddress;
                            }
                        }
                    }
                } catch (Throwable e) {
                    LOGGER.warn("Failed to retriving LocalAddress:", e);
                }
                inited.compareAndSet(false, true);
            }
        }
    }

}
