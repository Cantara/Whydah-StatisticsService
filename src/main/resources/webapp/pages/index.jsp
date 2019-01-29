<%--
  Created by IntelliJ IDEA.
  User: bardl
  Date: 15.05.2014
  Time: 07:41
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Statistics and Value Reporting</title>
</head>
<body>
    <h3>Implemented methods</h3>
    <li><a href="./gui/implemented?prefixOnly">List of prefix's</a> </li>
    <li><a href="./gui/implemented?prefix=dummy-load">List of methods within a prefix.</a> </li>


    <h3>Observations</h3>
    <li><a href="./gui/observations?prefix=dummy-load">List usage-count pr method.</a> </li>
    <li><a href="./gui/slainterval?prefix=initial&methodName=com.valuereporter.test">Graph of Count/time for a method and prefix.</a> </li>

    <h3>InUse - which methods are actually being used</h3>
    <li><a href="./gui/inuse?prefix=dummy-load">Show methods implemented vs. in use.</a> </li>


    <h3>Rest interface</h3>
    <li><a href="./observe/implementedprefix">List implemented prefixes</a> </li>
    <li><a href="./observe/implementedmethods/dummy-load">List implemented methods for prefix</a> </li>"

    ________
    <h3>Old stuff...</h3>
    <li><a href="./observe/observedmethods/{prefix}/{name}">template listing of operations (./observe/observedmethods/{prefix}/{name})</a></li>
    <b>Ping and Hello test</b>
    <li><a href="./observe/observedmethods/initial/com.valuereporter.test">Initial Data</a> </li>
    <li><a href="./observe/observedmethods/template-prefix/org.valuereporter.Welcome.ping">Ping</a>
    <li><a href="./observe/observedmethods/template-prefix/org.valuereporter.Welcome.hello">Helo</a>

    <h3>SLA</h3>
   <li>SLA reporting: <a href="./gui/sla?prefix=template-prefix&methodName=org.valuereporter.Welcome.ping">SLA reporting on Welcome.ping</a></li>
   <li>SLA Summary: <a href="./gui/slainterval?prefix=template-prefix&methodName=org.valuereporter.Welcome.ping">SLA reporting on Welcome.ping</a></li>
   <li>SLA rest: <a href="./observe/sla/observations/template-prefix?filter=org.valuereporter.Welcome.hello">http://localhost:4901/reporter/observe/sla/observations/{prefix}?filter={methodName}</a></li>

   <h3>Load Generator</h3>
   <li>SLA Interval/Summary: <a href="./gui/slainterval?prefix=dummy-load&methodName=org.dummy.load.LoadThread.performVisibleLoad">Graph of Count/time</a> </li>

</body>
</html>
