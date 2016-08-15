package openeet.lite;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLSocketFactoryTLS11 extends SSLSocketFactory {

	private SSLSocketFactory delegate;	
	private String[] enableProtocols;
	public SSLSocketFactoryTLS11(SSLSocketFactory delegate, String[] enableProtocols) {
		this.delegate=delegate;
		this.enableProtocols=enableProtocols;
	}
	
	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
		return enableTLSOnSocket(delegate.createSocket(socket, host, port, autoClose));
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return delegate.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return delegate.getSupportedCipherSuites();
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return enableTLSOnSocket(delegate.createSocket(host, port));
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return enableTLSOnSocket(delegate.createSocket(host, port));
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
			throws IOException, UnknownHostException {
		return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort));
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
	}
	
    private Socket enableTLSOnSocket(Socket socket) {
        if(socket != null && (socket instanceof SSLSocket)) {
            ((SSLSocket)socket).setEnabledProtocols(enableProtocols);
        }
        return socket;
    }

}
