package learn.zhujl.geo.util;

/**
 * 类CoordType.java的实现描述：坐标类型
 *
 * @author JianLin.Zhu 2015-9-22 下午7:26:36
 */
public enum CoordType {
    /**
     * WGS-84 支持厂商:苹果
     **/
    EARTH("EARTH", "地球坐标"),
    /**
     * GCJ-02 支持厂商:谷歌、高德
     **/
    MARS("MARS", "火星坐标"),
    /**
     * BD-09  支持厂商:百度
     **/
    BAIDU("BAIDU", "百度坐标"),
    /**
     * 搜狗       支持厂商：搜狗   http://map.sogou.com/api/
     **/
    SOGOU("SOGOU", "搜狗坐标"),
    /**
     * 图吧      支持厂商：图吧  http://open.mapbar.com/
     **/
    MAPBAR("MAPBAR", "搜狗坐标");

    private String name;
    private String remark;

    private CoordType(String name, String remark) {
        this.name = name;
        this.remark = remark;
    }

    public static CoordType codeOf(String name) {
        for (CoordType s : CoordType.values()) {
            if (equalsIgnoreCase(s.getName(), name)) {
                return s;
            }
        }

        return null;
    }

    private static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 != null ? str1.equalsIgnoreCase(str2) : str2 == null;
    }

    public static void main(String[] args) {
        System.out.println(CoordType.MARS.equals(CoordType.codeOf("Mars")));
        System.out.println(CoordType.MARS.equals(CoordType.codeOf("EARTH")));
        System.out.println(CoordType.SOGOU.equals(CoordType.codeOf("SOGOU")));
        System.out.println(CoordType.MAPBAR.equals(CoordType.codeOf("MAPBAR")));
        System.out.println(CoordType.codeOf("BAIDU"));
        System.out.println(CoordType.codeOf("mars"));
        System.out.println(CoordType.codeOf("sogou"));
        System.out.println(CoordType.codeOf("MAPBAr"));
        System.out.println(CoordType.codeOf(""));
        System.out.println(CoordType.codeOf(null));
    }

    public String getName() {
        return name;
    }

    public String getRemark() {
        return remark;
    }

    @Override
    public String toString() {
        return name;
    }
}
