package com.stylefeng.guns.rest.service.bena;

import java.util.List;

public class Seats {

    /**
     * limit : 5
     * ids : 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24
     * single : [[{"seatId":1,"row":1,"column":1},{"seatId":2,"row":1,"column":2},{"seatId":3,"row":1,"column":3},{"seatId":4,"row":1,"column":4},{"seatId":5,"row":1,"column":5},{"seatId":6,"row":1,"column":6}],[{"seatId":7,"row":2,"column":1},{"seatId":8,"row":2,"column":2},{"seatId":9,"row":2,"column":3},{"seatId":10,"row":2,"column":4},{"seatId":11,"row":2,"column":5},{"seatId":12,"row":2,"column":6}]]
     * couple : [[{"seatId":13,"row":3,"column":1},{"seatId":14,"row":3,"column":2},{"seatId":15,"row":3,"column":3},{"seatId":16,"row":3,"column":4},{"seatId":17,"row":3,"column":5},{"seatId":18,"row":3,"column":6}],[{"seatId":19,"row":4,"column":1},{"seatId":20,"row":4,"column":2},{"seatId":21,"row":4,"column":3},{"seatId":22,"row":4,"column":4},{"seatId":23,"row":4,"column":5},{"seatId":24,"row":4,"column":6}]]
     */

    private int limit;
    private String ids;
    private List<List<SingleBean>> single;
    private List<List<CoupleBean>> couple;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public List<List<SingleBean>> getSingle() {
        return single;
    }

    public void setSingle(List<List<SingleBean>> single) {
        this.single = single;
    }

    public List<List<CoupleBean>> getCouple() {
        return couple;
    }

    public void setCouple(List<List<CoupleBean>> couple) {
        this.couple = couple;
    }

    public static class SingleBean {
        /**
         * seatId : 1
         * row : 1
         * column : 1
         */

        private int seatId;
        private int row;
        private int column;

        public int getSeatId() {
            return seatId;
        }

        public void setSeatId(int seatId) {
            this.seatId = seatId;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }
    }

    public static class CoupleBean {
        /**
         * seatId : 13
         * row : 3
         * column : 1
         */

        private int seatId;
        private int row;
        private int column;

        public int getSeatId() {
            return seatId;
        }

        public void setSeatId(int seatId) {
            this.seatId = seatId;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }
    }
}
