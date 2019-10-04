package lib;

import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParser  {
    // Ham nhan JSOBJECT va tra ve list routers chua vi do va kinh do
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jSọnRoutes = null;
        JSONArray jSonLegs = null;
        JSONArray jSonSteps = null;
        JSONObject jSonDistance = null;
        JSONObject jSonDuration = null;
        JSONObject jSonStartLocation = null;
        JSONObject jSonEndlocation = null;
        String jSonEndAddress = null;
        String jSonStartAddress = null;

        try {

            jSọnRoutes = jObject.getJSONArray("routes");

            // duyet tat ca routers
            for(int i=0;i < jSọnRoutes.length();i++){
                jSonLegs = ( (JSONObject)jSọnRoutes.get(i)).getJSONArray("legs");
                // khoi tao list path kieu du lieu HashMap
                List path = new ArrayList<HashMap<String, String>>();

                // duyet tat ca legs
                for(int j=0;j < jSonLegs.length();j++){

                    // lay lat cua start_location tu json data
                    jSonStartLocation = ((JSONObject) jSonLegs.get(j)).getJSONObject("start_location");
                    Double latStart = jSonStartLocation.getDouble("lat");
                    HashMap<String,String> hmLatStartLocationLat = new HashMap<String, String>();
                    hmLatStartLocationLat.put("lat_start",Double.toString(latStart));
                    // lay lng cua start_location tu json data
                    Double lngStart = jSonStartLocation.getDouble("lng");
                    HashMap<String,String> hmLngStartLocation = new HashMap<String, String>();
                    hmLngStartLocation.put("lng_start",Double.toString(lngStart));

                    // lay lat cua end_location tu json data
                    jSonEndlocation = ((JSONObject) jSonLegs.get(j)).getJSONObject("end_location");
                    Double latEnd = jSonEndlocation.getDouble("lat");
                    HashMap<String,String> hmLatEndLocation = new HashMap<String, String>();
                    hmLatEndLocation.put("lat_end",Double.toString(latEnd));

                    // lay end_address tu json data
                    jSonEndAddress = ((JSONObject)jSonLegs.get(j)).getString("end_address");
                    HashMap<String,String> hmEndAddress = new HashMap<String, String>();
                    hmEndAddress.put("end_address",jSonEndAddress);

                    // lay start_address tu json data

                    jSonStartAddress = ((JSONObject)jSonLegs.get(j)).getString("start_address");
                    HashMap<String,String> hmStartAddress = new HashMap<String, String>();
                    hmStartAddress.put("start_address",jSonStartAddress);

                    // lay lng cua end_location tu json data
                    Double lngEnd = jSonEndlocation.getDouble("lng");
                    HashMap<String,String> hmLngEndLocation = new HashMap<String, String>();
                    hmLngEndLocation.put("lng_end",Double.toString(lngEnd));

                    // Lay distance tu json data */
                    jSonDistance = ((JSONObject) jSonLegs.get(j)).getJSONObject("distance");
                    HashMap<String, String> hmDistance = new HashMap<String, String>();
                    hmDistance.put("distance", jSonDistance.getString("text"));

                    // Lay duration tu json data */
                    jSonDuration = ((JSONObject) jSonLegs.get(j)).getJSONObject("duration");
                    HashMap<String, String> hmDuration = new HashMap<String, String>();
                    hmDuration.put("duration", jSonDuration.getString("text"));

                    // add lat object and lng object cua diem bat dau vao path
                     path.add(hmLatStartLocationLat);
                     path.add(hmLngStartLocation);

                    // add lat object and lng object cua diem ket thuc vao path
                    path.add(hmLatEndLocation);
                    path.add(hmLngEndLocation);

                    // add end_address vao path
                    path.add(hmEndAddress);
                    // add start_address vao path
                    path.add(hmStartAddress);

                    // add distance object vao path
                    path.add(hmDistance);
                    // add duration object vao path
                    path.add(hmDuration);
                    jSonSteps = ( (JSONObject)jSonLegs.get(j)).getJSONArray("steps");

                    // Duyet vong lap for qua tat ca steps
                    for(int k=0;k < jSonSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSonSteps.get(k)).get("polyline")).get("points");
                        List list = decodePolyLine(polyline);

                        // Duyet vong lap for qua tat ca points
                        for(int l=0; l <list.size();l++){

                            // khoi tao HashMap put toan bo cac kinh do va vi do vao HashMap
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hashMap.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            // add HashMap chua toan bo kinh do va vi do vao path
                            path.add(hashMap);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return routes;
    }

    // ham decodePolyLine from Google Map direction API
    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }
        return decoded;
    }
}
