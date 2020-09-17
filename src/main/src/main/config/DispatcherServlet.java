package main.config;

import main.annotation.Autowired;
import main.annotation.Controller;
import main.annotation.RequestMapping;
import main.annotation.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liujipeng
 * @date 2020/9/4 15:20
 * @mail xuxiejp@163.com
 * @desc ...
 */

public class DispatcherServlet extends HttpServlet {
    //封装扫描到的类
    private List<String> names = new LinkedList<>();
    //key : BeanName   value : Bean
    private Map<String,Object> beans = new ConcurrentHashMap<>();
    //key : requestURL    value : Request.index + Method.index
    private Map<String,Method> methods = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = requestURI.replace(contextPath, "");
        Method method = methods.get(path);
        try {
            Object invoke = method.invoke(beans.get("/"+path.split("/")[1]), null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        super.service(req, res);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // 扫描路径下所有class
            scanPackage("main");
            //将扫描到的类进行初始化
            initializeBean();
            //将 类里面的自动注入设置属性值，requestURL装配请求method
            adapterRequestPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void adapterRequestPath() throws IllegalAccessException {
        if(CollectionUtils.isEmpty(beans)){
            return;
        }
        Set<Map.Entry<String, Object>> entries = beans.entrySet();
        for ( Map.Entry<String, Object> obj:entries) {
            Field[] declaredFields = obj.getValue().getClass().getDeclaredFields();
            for (Field fie:declaredFields) {
                //将注入对象 反射设置到对应的 属性对象
                if(fie.isAnnotationPresent(Autowired.class)){
                    fie.getName();
                    fie.setAccessible(true);
                    fie.set(obj.getValue(),beans.get(fie.getName()));
                }
            }
            Method[] declaredMethods = obj.getValue().getClass().getDeclaredMethods();
            for (Method meth:declaredMethods) {
                //将 Request.index + Method.index 拼接
                if(meth.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping annotation = meth.getAnnotation(RequestMapping.class);
                    String space = annotation.value()[0];
                    methods.put(obj.getKey()+space,meth);
                }
            }
        }
    }

    private void initializeBean() throws Exception {
        if(CollectionUtils.isEmpty(names)){
            return;
        }
        for (String name:names) {
            Class<?> aClass = Class.forName(name);
            if(aClass.isAnnotationPresent(Controller.class)){
                Object obj = aClass.newInstance();
                RequestMapping annotation = aClass.getAnnotation(RequestMapping.class);
                String name1 = annotation.value()[0];
                beans.put(name1,obj);
            }
            if(aClass.isAnnotationPresent(Service.class)){
                Object obj = aClass.newInstance();
                //获取类名
                String shortClassName = ClassUtils.getShortName(aClass.getName());
                //首字母小写
                String decapitalize = Introspector.decapitalize(shortClassName);
                beans.put(decapitalize,obj);
            }
        }
    }

    private void scanPackage(String main) {
        URL resource = this.getClass().getClassLoader().getResource(main.replaceAll("\\.", "/"));
        String path = resource.getFile();
        File file = new File(path);
        File[] files = file.listFiles();
        for (File fil:files) {
            if(fil.isDirectory()){
                scanPackage(main+"."+fil.getName());
            }else{
                names.add(main+"."+fil.getName().replace(".class",""));
            }
        }
    }

    /**
     * 把字符串的首字母小写
     *
     * @param name
     * @return
     */
    private String toLowerFirstWord(String name) {
        char[] charArray = name.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }

}
