package pl.lodz.p.edu.s195738.cbr.interceptors;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;


public class TrackerInterceptor {

    @Resource
    private SessionContext sctx;
    private static final Logger log = Logger.getLogger(TrackerInterceptor.class.getName());

    /**
     *
     * @param ictx
     * @return
     * @throws Exception
     */
    @AroundInvoke
    public Object traceInvoke(InvocationContext ictx) throws Exception {

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");

        StringBuilder message = new StringBuilder();
        message.append("[")
               .append(simpleTimeFormat.format(new Date()))
               .append("] Wywołanie metody: ")
               .append(ictx.getMethod().toString())
               .append(" użytkownik: ")
               .append(sctx.getCallerPrincipal().getName())
               .append(" wartości parametrów: ");

        Object[] params = ictx.getParameters();
        if (params != null) {
            for (Object param : params) {
                if (null == param) {
                    message.append("null ");
                } else {
                    message.append(param.toString())
                           .append(" ");
                }
            }
        }
        message.append(params);

        Object result = null;
        try {
            result = ictx.proceed();
        } catch (Exception e) {
            message.append(" zakończone wyjątkiem: ")
                   .append(e.toString());
            log.severe(message.toString());
            throw e;
        }

        message.append(" wartość zwrócona: ");
        if (null == result) {
            message.append("null ");
        } else {
            message.append(result.toString())
                   .append(" ");
        }
        log.severe(message.toString());

        return result;
    }
}
