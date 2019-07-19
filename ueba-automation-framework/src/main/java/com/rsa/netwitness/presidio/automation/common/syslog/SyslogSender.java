package com.rsa.netwitness.presidio.automation.common.syslog;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;

import java.io.IOException;

public class SyslogSender {
    public static void main(String arg[]) throws IOException {
        // Initialise sender
        TcpSyslogMessageSender messageSender = new TcpSyslogMessageSender();
        //messageSender.setDefaultMessageHostname("dev-lirana"); // some syslog cloud services may use this field to transmit a secret key
        //messageSender.setDefaultAppName("Logpoint_UEBA");
        messageSender.setDefaultFacility(Facility.ALERT);
        messageSender.setDefaultSeverity(Severity.ALERT);
        messageSender.setSyslogServerHostname("fs-logpoint-01");
        messageSender.setSyslogServerPort(514);
        //messageSender.setMessageFormat(MessageFormat.RFC_3164); // optional, default is RFC 3164
        messageSender.setSocketConnectTimeoutInMillis(2000);
        //messageSender.setSsl(true);

        // send a SyslogSender message
        messageSender.sendMessage("KKROOTDC06.kk.dk MSWinEventLog\t1\tSecurity\t52762038\tWed Nov 30 07:35:00 2017\t4769\tMicrosoft-Windows-Security-Auditing\tN/A\tN/A\tSuccess Audit\tKKROOTDC06.kk.dk\tKerberos Service Ticket Operations\t\tA Kerberos service ticket was requested.    Account Information:   Account Name:  GB3J   Account Domain:  KK.DK   Logon GUID:  {36C89728-BEA9-FEBE-6311-04A4B9BDE535}    Service Information:   Service Name:  ASDFGHJK   Service ID:  S-1-5-21-1292428093-963894560-1801674531-30147    Network Information:   Client Address:  ::ffff:10_46_101_110.logpoint.local   Client Port:  49763    Additional Information:   Ticket Options:  0x40810000   Ticket Encryption Type: 0x12   Failure Code:  0x0   Transited Services: -    This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.    This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.    Ticket options, encryption types, and failure codes are defined in RFC 4120.\r\n" +
                "KKROOTDC06.kk.dk MSWinEventLog\t1\tSecurity\t52762038\tWed Nov 30 07:35:00 2017\t4769\tMicrosoft-Windows-Security-Auditing\tN/A\tN/A\tSuccess Audit\tKKROOTDC06.kk.dk\tKerberos Service Ticket Operations\t\tA Kerberos service ticket was requested.    Account Information:   Account Name:  GB3J   Account Domain:  KK.DK   Logon GUID:  {36C89728-BEA9-FEBE-6311-04A4B9BDE535}    Service Information:   Service Name:  ZXCVBNM   Service ID:  S-1-5-21-1292428093-963894560-1801674531-30147    Network Information:   Client Address:  ::ffff:10_46_101_110.logpoint.local   Client Port:  49763    Additional Information:   Ticket Options:  0x40810000   Ticket Encryption Type: 0x12   Failure Code:  0x0   Transited Services: -    This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.    This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.    Ticket options, encryption types, and failure codes are defined in RFC 4120.");
    }
}