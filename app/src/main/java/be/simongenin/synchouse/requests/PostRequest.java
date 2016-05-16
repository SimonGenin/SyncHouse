package be.simongenin.synchouse.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * @author Simon Genin
 *
 * The post request object, create out of the StringRequest object
 * provided by Volley.
 */
public class PostRequest extends StringRequest {

    private Map<String, String> params;

    public PostRequest(String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}