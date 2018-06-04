package com.knox.oauth.admin.commons.exception;

/**
 * Clase que representa una excepcion controlada del mosulo SSO
 * 
 * @author team ssoinnova4j
 *
 */

public class SSODataException extends Throwable {

	private static final long serialVersionUID = -2699254684308703614L;
	private String mensaje;
	private Throwable causa;
	private String codigoErr;
	
	/**
	 * Default
	 */
	public SSODataException() {
		
	}
	
	/**
	 * @param mensaje
	 * @param causa
	 * @param codigoErr
	 */
	public SSODataException(String mensaje, Throwable causa, String codigoErr) {
		this.mensaje = mensaje;
		this.causa = causa;
		this.codigoErr = codigoErr;
	}
	/**
	 * @return the mensaje
	 */
	public String getMensaje() {
		return mensaje;
	}
	/**
	 * @param mensaje the mensaje to set
	 */
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	/**
	 * @return the causa
	 */
	public Throwable getCausa() {
		return causa;
	}
	/**
	 * @param causa the causa to set
	 */
	public void setCausa(Throwable causa) {
		this.causa = causa;
	}
	/**
	 * @return the codigoErr
	 */
	public String getCodigoErr() {
		return codigoErr;
	}
	/**
	 * @param codigoErr the codigoErr to set
	 */
	public void setCodigoErr(String codigoErr) {
		this.codigoErr = codigoErr;
	}

}
