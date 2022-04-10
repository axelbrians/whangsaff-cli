package com.whangsaff.app.common.socket

import kotlin.Throws
import java.io.IOException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.KeyStoreException
import java.net.SocketException
import com.whangsaff.app.common.socket.SSLSocketKeystoreFactory.SecureType
import javax.net.ssl.SSLSocket
import java.net.InetAddress
import java.lang.NullPointerException
import java.io.File
import com.whangsaff.app.common.socket.SSLSocketKeystoreFactory
import java.io.FileInputStream
import java.io.InputStream
import java.net.Proxy
import javax.net.ssl.X509TrustManager
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import java.net.Socket
import java.security.cert.CertificateException
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.TrustManager

/*
 *  Copyright (C) 2015 Gabriel POTTER (gpotter2)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */ /**
 * Util class to create SSLSocket using a KeyStore certificate to connect a server
 *
 * @author gpotter2
 */
object SSLSocketKeystoreFactory {
    private var instance: String? = null

    /**
     * Instantiate sslsocket
     *
     * @param ip The IP to connect the socket to
     * @param port The port of the socket
     * @param pathToCert The path to the KeyStore cert (can be with getClass().getRessource()....)
     * @param passwordFromCert The password of the KeyStore cert
     * @param type The SSL algorithm to use
     * @return The SSLSocket or null if the connection was not possible
     * @throws IOException If the socket couldn't be created
     * @throws KeyManagementException  If the KeyManager couldn't be loaded
     * @throws CertificateException If the certificate is not correct (null or damaged) or the password is incorrect
     * @throws NoSuchAlgorithmException If the certificate is from an unknown type
     * @throws KeyStoreException If your system is not compatible with JKS KeyStore certificates
     * @author gpotter2
     */
    @Throws(
        IOException::class,
        KeyManagementException::class,
        NoSuchAlgorithmException::class,
        CertificateException::class,
        KeyStoreException::class,
        SocketException::class
    )
    fun getSocketWithCert(
        ip: String?,
        port: Int,
        pathToCert: String?,
        passwordFromCert: String,
        type: SecureType
    ): SSLSocket {
        val ip2 = InetAddress.getByName(ip)
        if (ip2 == null) {
            NullPointerException("The ip must be a correct IP !").printStackTrace()
        }
        val f = File(pathToCert)
        if (!f.exists()) {
            throw NullPointerException("The specified path point to a non existing file !")
        }
        return getSocketWithCert(ip2, port, FileInputStream(f), passwordFromCert, type)
    }

    /**
     * Instantiate sslsocket
     *
     * @param ip The IP to connect the socket to
     * @param port The port of the socket
     * @param pathToCert The path to the KeyStore cert (can be with getClass().getRessourceAsStream()....)
     * @param passwordFromCert The password of the KeyStore cert
     * @param type The SSL algorithm to use
     * @return The SSLSocket or null if the connection was not possible
     * @throws IOException If the socket couldn't be created
     * @throws KeyManagementException  If the KeyManager couldn't be loaded
     * @throws CertificateException If the certificate is not correct (null or damaged) or the password is incorrect
     * @throws NoSuchAlgorithmException If the certificate is from an unknown type
     * @throws KeyStoreException If your system is not compatible with JKS KeyStore certificates
     * @author gpotter2
     */
    @Throws(
        IOException::class,
        KeyManagementException::class,
        NoSuchAlgorithmException::class,
        CertificateException::class,
        KeyStoreException::class,
        SocketException::class
    )
    fun getSocketWithCert(
        ip: String,
        port: Int,
        pathToCert: InputStream,
        passwordFromCert: String,
        type: SecureType
    ): SSLSocket? {
        val ip2 = InetAddress.getByName(ip)
        if (ip2 == null) {
            NullPointerException("The ip must be a correct IP !").printStackTrace()
            return null
        }
        return getSocketWithCert(ip2, port, pathToCert, passwordFromCert, type)
    }

    /**
     * Instantiate sslsocket
     *
     * @param ip The IP to connect the socket to
     * @param port The port of the socket
     * @param pathToCert The path to the KeyStore cert (can be with getClass().getRessource()....)
     * @param passwordFromCert The password of the KeyStore cert
     * @param type The SSL algorithm to use
     * @return The SSLSocket or null if the connection was not possible
     * @throws IOException If the socket couldn't be created
     * @throws KeyManagementException  If the KeyManager couldn't be loaded
     * @throws CertificateException If the certificate is not correct (null or damaged) or the password is incorrect
     * @throws NoSuchAlgorithmException If the certificate is from an unknown type
     * @throws KeyStoreException If your system is not compatible with JKS KeyStore certificates
     * @author gpotter2
     */
    @Throws(
        IOException::class,
        KeyManagementException::class,
        NoSuchAlgorithmException::class,
        CertificateException::class,
        KeyStoreException::class,
        SocketException::class
    )
    fun getSocketWithCert(
        ip: InetAddress?,
        port: Int,
        pathToCert: String,
        passwordFromCert: String,
        type: SecureType
    ): SSLSocket {
        val f = File(pathToCert)
        if (!f.exists()) {
            throw NullPointerException("The specified path point to a non existing file !")
        }
        return getSocketWithCert(ip, port, FileInputStream(f), passwordFromCert, type)
    }

    /**
     * Instantiate sslsocket
     *
     * @param ip The IP to connect the socket to
     * @param port The port of the socket
     * @param pathToCert The path to the KeyStore cert (can be with getClass().getRessourceAsStream()....)
     * @param passwordFromCert The password of the KeyStore cert
     * @param type The SSL algorithm to use
     * @return The SSLSocket or null if the connection was not possible
     * @throws IOException If the socket couldn't be created
     * @throws KeyManagementException  If the KeyManager couldn't be loaded
     * @throws CertificateException If the certificate is not correct (null or damaged) or the password is incorrect
     * @throws NoSuchAlgorithmException If the certificate is from an unknown type
     * @throws KeyStoreException If your system is not compatible with JKS KeyStore certificates
     * @author gpotter2
     */
    @Throws(
        IOException::class,
        KeyManagementException::class,
        NoSuchAlgorithmException::class,
        CertificateException::class,
        KeyStoreException::class,
        SocketException::class
    )
    fun getSocketWithCert(
        ip: InetAddress?,
        port: Int,
        pathToCert: InputStream?,
        passwordFromCert: String,
        type: SecureType
    ): SSLSocket {
        val tmm: Array<X509TrustManager?>?
        val ks = KeyStore.getInstance(instance)
        ks.load(pathToCert, passwordFromCert.toCharArray())
        tmm = tm(ks)
        val ctx = SSLContext.getInstance(type.type)
        ctx.init(null, tmm, null)
        val SocketFactory = ctx.socketFactory as SSLSocketFactory
        return SocketFactory.createSocket(ip, port) as SSLSocket
    }

    /**
     * Instantiate sslsocket (beta proxy)
     *
     * @param ip The IP to connect the socket to
     * @param port The port of the socket
     * @param pathToCert The path to the KeyStore cert (can be with getClass().getRessourceAsStream()....)
     * @param passwordFromCert The password of the KeyStore cert
     * @param type The SSL algorithm to use
     * @return The SSLSocket or null if the connection was not possible
     * @throws IOException If the socket couldn't be created
     * @throws KeyManagementException  If the KeyManager couldn't be loaded
     * @throws CertificateException If the certificate is not correct (null or damaged) or the password is incorrect
     * @throws NoSuchAlgorithmException If the certificate is from an unknown type
     * @throws KeyStoreException If your system is not compatible with JKS KeyStore certificates
     * @author gpotter2
     */
    @Throws(
        IOException::class,
        KeyManagementException::class,
        NoSuchAlgorithmException::class,
        CertificateException::class,
        KeyStoreException::class,
        SocketException::class
    )
    fun getSocketWithCert(
        ip: InetAddress,
        port: Int,
        pathToCert: InputStream?,
        passwordFromCert: String,
        type: SecureType,
        proxy: Proxy?
    ): SSLSocket {
        val tmm: Array<X509TrustManager?>?
        val ks = KeyStore.getInstance(instance)
        ks.load(pathToCert, passwordFromCert.toCharArray())
        tmm = tm(ks)
        val ctx = SSLContext.getInstance(type.type)
        ctx.init(null, tmm, null)
        val SocketFactory = ctx.socketFactory as SSLSocketFactory
        val proxy_s = Socket(proxy)
        return SocketFactory.createSocket(proxy_s, ip.hostAddress, port, true) as SSLSocket
    }

    /**
     * Util class to get the X509TrustManager
     *
     *
     * @param keystore
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @author gpotter2
     */
    @Throws(NoSuchAlgorithmException::class, KeyStoreException::class)
    private fun tm(keystore: KeyStore): Array<X509TrustManager?>? {
        val trustMgrFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustMgrFactory.init(keystore)
        val trustManagers = trustMgrFactory.trustManagers
        for (i in trustManagers.indices) {
            if (trustManagers[i] is X509TrustManager) {
                val tr = arrayOfNulls<X509TrustManager>(1)
                tr[0] = trustManagers[i] as X509TrustManager
                return tr
            }
        }
        return null
    }

    /**
     *
     * A SSL algorithms types chooser enum
     *
     * @author gpotter2
     */
    enum class SecureType(val type: String) {
        SSL("SSL"), SSLv2("SSLv2"), SSLv3("SSLv3"), TLS("TLS"), TLSv1("TLSv1"), TLSv1_1("TLSv1.1"), TLSv1_2("TLSv1.2");

    }

    /**
     * CONFIGURATION SECTION
     */
    init {
        instance = "JKS" /* TODO REPLACE WITH BKS IF USING IT*/
        /*
         * Several Notes:
         * - Android only works with BKS, so you need to use only BKS certs files
         * - As before Android 15, BKS-v1 was used, you need to convert BKS in BKS-v1 to use it in Android 15-; BUT as Android 23+ doesn't support BKS-v1
         * and as BKS-v1 is deprecated, you need to have both of the certs and use them in fuction of the version
         * - Java doesn't support BKS without library
         * - A BKS format client can be connected a JKS format server
         */
    }
}