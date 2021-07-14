package com.xinchen.tool.httptrace.framework.common.loader;



import com.xinchen.tool.httptrace.framework.common.loader.scen.Hello;
import com.xinchen.tool.httptrace.framework.common.loader.scen.HelloChinese;
import com.xinchen.tool.httptrace.framework.common.loader.scen.HelloEnglish;
import com.xinchen.tool.httptrace.framework.common.loader.scen.HelloFrench;
import com.xinchen.tool.httptrace.framework.common.loader.scen.HelloLatin;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
/**
 *
 * {@link LoadLevel} - order越大加载的优先级越高
 *
 * @date 2021-07-13 16:01
 */
public class EnhancedServiceLoaderTest {
  /**
   * Test load by class and class loader.
   */
  @Test
  public void testLoadByClassAndClassLoader() {
    Hello load = EnhancedServiceLoader.load(Hello.class, Hello.class.getClassLoader());
    Assertions.assertEquals(load.say(), "Olá.");
  }


  /**
   * Test load exception.
   */
  @Test
  public void testLoadException() {
    Assertions.assertThrows(EnhancedServiceNotFoundException.class, () -> {
      EnhancedServiceLoaderTest load = EnhancedServiceLoader.load(EnhancedServiceLoaderTest.class);
    });
  }

  /**
   * Test load by class.
   */
  @Test
  public void testLoadByClass() {
    Hello load = EnhancedServiceLoader.load(Hello.class);
    assertThat(load.say()).isEqualTo("Olá.");
  }


  /**
   * Test load by class and activate name.
   */
  @Test
  public void testLoadByClassAndActivateName() {
    Hello englishHello = EnhancedServiceLoader.load(Hello.class, "HelloEnglish");
    assertThat(englishHello.say()).isEqualTo("hello!");
  }


  /**
   * Test load by class and class loader and activate name.
   */
  @Test
  public void testLoadByClassAndClassLoaderAndActivateName() {
    Hello englishHello = EnhancedServiceLoader
        .load(Hello.class, "HelloEnglish", EnhancedServiceLoaderTest.class.getClassLoader());
    assertThat(englishHello.say()).isEqualTo("hello!");
  }

  /**
   * Gets all extension class.
   */
  @Test
  public void getAllExtensionClass() {
    List<Class> allExtensionClass = EnhancedServiceLoader.getAllExtensionClass(Hello.class);
    assertThat(allExtensionClass.get(3).getSimpleName()).isEqualTo((HelloLatin.class.getSimpleName()));
    assertThat(allExtensionClass.get(2).getSimpleName()).isEqualTo((HelloFrench.class.getSimpleName()));
    assertThat(allExtensionClass.get(1).getSimpleName()).isEqualTo((HelloEnglish.class.getSimpleName()));
    assertThat(allExtensionClass.get(0).getSimpleName()).isEqualTo((HelloChinese.class.getSimpleName()));
  }

  /**
   * Gets all extension class 1.
   */
  @Test
  public void getAllExtensionClass1() {
    List<Class> allExtensionClass = EnhancedServiceLoader
        .getAllExtensionClass(Hello.class, ClassLoader.getSystemClassLoader());
    assertThat(allExtensionClass).isNotEmpty();
  }

  @Test
  public void getSingletonExtensionInstance(){
    Hello hello1 = EnhancedServiceLoader.load(Hello.class, "HelloChinese");
    Hello hello2 = EnhancedServiceLoader.load(Hello.class, "HelloChinese");
    assertThat(hello1 == hello2).isTrue();
  }

  @Test
  public void getMultipleExtensionInstance(){
    Hello hello1 = EnhancedServiceLoader.load(Hello.class, "HelloLatin");
    Hello hello2 = EnhancedServiceLoader.load(Hello.class, "HelloLatin");
    assertThat(hello1 == hello2).isFalse();
  }

  @Test
  public void getAllInstances(){
    List<Hello> hellows1 = EnhancedServiceLoader.loadAll(Hello.class);
    List<Hello> hellows2 = EnhancedServiceLoader.loadAll(Hello.class);
    for (Hello hello : hellows1){
      if (!hello.say().equals("Olá.")) {
        assertThat(hellows2.contains(hello)).isTrue();
      }
      else{
        assertThat(hellows2.contains(hello)).isFalse();
      }
    }
  }
}