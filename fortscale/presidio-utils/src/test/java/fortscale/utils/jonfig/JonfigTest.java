package fortscale.utils.jonfig;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;



/**
 * Tests for {@link DomainSitter}.
 *
 *@author ronb@fortsclae.com (Ron Begleiter)
 */
//@RunWith(JUnit4.class)
public class JonfigTest {
	
	private static final String testDataDir = getTestDateDir();
	
	
	private static String getTestDateDir(){
		String os = System.getProperty("os.name");
		String ret = "./src/test/etc/";
		if(os.startsWith("Windows")){
			ret = "src\\test\\etc\\";
		}
		return ret;
	}

  @Test
  public void thisAlwaysPasses() {
  }

  @Test
  @Ignore
  public void thisIsIgnored() {
  }


  @Test
  public void testSimpleFonfig() throws Exception {
    Object context = Jonfig.get(testDataDir + "fonfig_simple1.json" );
    @SuppressWarnings("unchecked")
	Map<String,Object> helloworld = (Map<String,Object>)context;
    Assert.assertEquals("value missing","Hello" , helloworld.get("f1"));
    Assert.assertEquals("value missing","World!", helloworld.get("f2"));
  }

  @Test
  public void testArrFonfig() throws Exception {
    @SuppressWarnings("unchecked")
	List<Object> ll = (List<Object>)Jonfig.get(testDataDir + "fonfig_arr.json" );
    StringBuffer sb_hatejava = new StringBuffer( 100 );
    for( Object obj : ll ){ 
      sb_hatejava.append( obj.toString() ).append( " " );
      Assert.assertEquals( "should be a string", String.class, obj.getClass( ));
    }
    Assert.assertEquals( "content failure", "this is not a love song ", sb_hatejava.toString());
  }

}
