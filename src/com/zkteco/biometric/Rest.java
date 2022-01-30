package com.zkteco.biometric;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class Rest extends Thread {

	public static String URL="http://ssjv1.test/api/";
	public static String INNAJ = "buscar_innaj/";
	public static String INNAJ_FINGER = "registrar_huella";
	
	private final int BUSCAR_INNAJ = 1;
	private final int REGISTRAR_HUELLA = 2;
	
	private String response;
	private Innaj innaj;
	
	private boolean corriendo = true;
	private Thread t;
	private int id;
	private String parametros;
	private String Respuesta=null;
//	public Rest(Innaj innaj){
//		this.innaj = innaj;
//	}
//	
	public void EnviarGet(int id, String parametros, Thread t)
	{
		this.id = id;
		this.parametros = parametros;
		this.t = t;
	}
	
	public  void GetRestful(String domain, String route) throws Exception
	{
		String output ="";
		 StringBuilder builder = new StringBuilder();
		try {

            URL url = new URL(domain+route);//your url i.e fetch data from .
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "
                        + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            
            while ((output = br.readLine()) != null) {
//                System.out.println(output);
                builder.append(output);
            }
            this.Respuesta = builder.toString();
//            setResponse(builder.toString());
    		System.out.println(this.Respuesta);
            conn.disconnect();

        } catch (Exception e) {
            System.out.println("Exception in NetClientGet:- " + e);
        }
		
		
		
		
	}
	
	public void EnviarPost(int id, String parametros, Thread t)
	{
		this.id = id;
		this.parametros = parametros;
		this.t = t;
	}
	public void PosRestful(String domain, String route, String urlParameters)
	 { 
	 
		 String output ="";
		 StringBuilder builder = new StringBuilder();
	        try{
//	        String urlParameters  = "param1=a&param2=b&param3=c";
	            byte[] postData       = urlParameters.getBytes();
	            int    postDataLength = postData.length;
	    //        String request        = "http://example.com/index.php";
	            System.out.println("ruta: "+domain+route );
	            URL url= new URL( domain+route );
	            HttpURLConnection con= (HttpURLConnection) url.openConnection();
	            con.setDoOutput(true);
//	            String basicAuth = "Basic " + new String(Base64.encode((user + ":" + pass).getBytes(), Base64.NO_WRAP));
//	            con.setRequestProperty("Authorization", basicAuth);
	            con.setConnectTimeout(30000);
	            con.setReadTimeout(30000);
	            con.setInstanceFollowRedirects(true);
	            con.setRequestMethod("POST");
	            con.setRequestProperty( "Content-Type", "application/json");
//	            conn.setRequestProperty( "charset", "utf-8");
	            con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
	            con.setUseCaches(false);




	            DataOutputStream wr = new DataOutputStream( con.getOutputStream());
	            wr.write(postData);

	            if (con.getResponseCode() != 200) {
	                throw new RuntimeException("Failed : HTTP Error code : "
	                        + con.getResponseCode());
	            }
	            
	            InputStreamReader in = new InputStreamReader(con.getInputStream());
	            BufferedReader br = new BufferedReader(in);
	            
	            while ((output = br.readLine()) != null) {
//	                System.out.println(output);
	                builder.append(output);
	            }
	            
	            this.Respuesta = builder.toString();
//	            setResponse(builder.toString());
	    		System.out.println(this.Respuesta);
	            
	            con.disconnect();

	            
	        }catch (Exception e){

	        	 System.out.println("Exception in NetClientGet:- " + e);
	        }
	        this.setResponse(builder.toString());
	       
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Innaj getInnaj() {
		return innaj;
	}

	public void setInnaj(Innaj innaj) {
		this.innaj = innaj;
	}
	
	public String getRespuesta() {
		return Respuesta;
	}

	public void setRespuesta(String respuesta) {
		Respuesta = respuesta;
	}

	public void run()
	{
		
			
	               
	                Enviar();
	               
//	                rest.setRespuesta(this.Respuesta);
//	                rest.setCodigoRespuesta(respCode);
//	                t.notify();
	                
	                t.start();
		
	}
	
	private void Enviar()
    {
		try {
			
			  switch (id) {
			  	case BUSCAR_INNAJ:
				  GetRestful(URL, INNAJ+parametros);
				  
				break;
				
			  	case REGISTRAR_HUELLA:
			  		PosRestful(URL, INNAJ_FINGER, parametros);

				default:
					break;
				}
		} catch (Exception e) {
			// TODO: handle exception
		}
	
    }
	
	
	
}
