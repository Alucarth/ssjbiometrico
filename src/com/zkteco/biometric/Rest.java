package com.zkteco.biometric;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class Rest {

	public static String URL="http://ssjv1.test/api/";
	public static String INNAJ = "buscar_innaj/";
	public static String INNAJ_FINGER = "registrar_huella";
	
	private String response;
	private Innaj innaj;
	
//	public Rest(Innaj innaj){
//		this.innaj = innaj;
//	}
//	
	
	public  void GetRestful(String domain, String route)
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
            System.out.print(builder.toString());
            this.innaj = new Innaj();
            this.innaj.parseJSON(builder.toString());
            conn.disconnect();

        } catch (Exception e) {
            System.out.println("Exception in NetClientGet:- " + e);
        }
		
		
		
		
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
	            URL url= new URL( domain+route );
	            HttpURLConnection con= (HttpURLConnection) url.openConnection();
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
	
	
}
