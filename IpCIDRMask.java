package databricks;

import java.lang.reflect.Array;
import java.util.*;

public class IpCIDRMask {

    //xxxxxxxx.xxxxxxxx.xxxxxxxx.xxxxxxxx (32 bits)
    //        0~255.0~255

    public boolean isAllowed(String ip, List<String[]> rules) {
        long ipNum = convertToNum(ip);

        boolean allow = false;
        for (String[] rule : rules) {
            Mask mask = convertRuleToMask(rule[0]);
            if (ipNum >= mask.ipNum && ipNum <= mask.ipNum + mask.range) {
                allow = parseRule(rule[1]);
                // return;
            }
        }

        return allow;
    }

    private boolean parseRule(String rule) {
        switch (rule) {
            case "ALLOW":
                return true;
            case "DENY":
                return false;
            default:
                return false;
        }
    }

    private long convertToNum(String ip) {
        String[] numStrs = ip.split("\\.");
        long number = 0L;
        for (String numStr : numStrs) {
            number = number * 256 + Long.parseLong(numStr);
        }

        return number;
    }

    private Mask convertRuleToMask(String mask) {
        String[] parts = mask.split("/");
        String numStrs = parts[0];
        int maskStr = Integer.parseInt(parts[1]);

        long num = convertToNum(numStrs);
        long range = (long) Math.pow(2, 32 - maskStr) - 1;

        return new Mask(num, range);
    }

    public static void main(String[] args) {
        IpCIDRMask ipCIDRMask = new IpCIDRMask();
        List<String[]> rules = new ArrayList<>();
        rules.add(new String[]{"192.168.0.1/32", "ALLOW"});
        rules.add(new String[]{"192.168.0.1/30", "ALLOW"});
        boolean x = ipCIDRMask.isAllowed("192.168.0.4", rules);
        System.out.println(x);
    }
}

class Mask {
    public long ipNum;
    public long range;

    public Mask(long ipNum, long range) {
        this.ipNum = ipNum;
        this.range = range;
    }
}

