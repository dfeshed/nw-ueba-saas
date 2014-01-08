package fortscale.utils.jonfig;

import java.util.*;
import java.io.*;
import com.fasterxml.jackson.core.*;

import fortscale.utils.logging.Logger;

/**
 *  JSON based configuration 
 *  Be lite / simple / fast (simply fastlite..)
 */
public final class Jonfig {
	private static final Logger logger = Logger.getLogger(Jonfig.class);
  private static Delegate delegate = null;

  private Jonfig(){  
    /* no instances */
  }

  // lazy initialize
  private static Delegate delegate( ) {
    if (delegate == null) {
      synchronized (Jonfig .class){
        if (delegate == null) {
          delegate = new FileDelegate();
        }
      }
    }
    return delegate;
  }

  public static Object get(String key) throws Exception {
    return delegate( ).get( key );
  }



// 00P:$h1t
  private static interface Delegate{
    Object get(String config_key) throws Exception;
  }

  private static class FileDelegate implements Delegate{

    public Object get(String config_key) throws Exception {
      JsonParser jp = new JsonFactory().createParser( new File( config_key ) );
      jp.nextToken( );
      Object calc = parse_json_val( jp );
      jp.close( );
      logger.info("input: {}, output: {}", config_key, calc);
      return calc;
    }

    private Object parse_json_val( JsonParser jp ) throws Exception {
        if( jp.getCurrentToken( ) == JsonToken.START_ARRAY ) {
          ArrayList<Object> larr = new ArrayList<Object>( );
          while( jp.nextToken( ) != JsonToken.END_ARRAY ){ 
            larr.add( parse_json_val( jp ) );
          }
          return larr;
        } else if( jp.getCurrentToken( ) == JsonToken.START_OBJECT ) {
          HashMap<String, Object> map = new HashMap<String,Object> ( );
          while( jp.nextToken( ) != JsonToken.END_OBJECT ) {
            String name = jp.getCurrentName( );
            jp.nextToken( );
            map.put( name, parse_json_val( jp ) );
          }
          return map;
        } else {
          return jp.getValueAsString( );
        }
    }

  }

}


