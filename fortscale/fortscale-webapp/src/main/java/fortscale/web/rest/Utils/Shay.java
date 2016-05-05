package fortscale.web.rest.Utils;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * Created by shays on 03/05/2016.
 */
public class Shay {

        private String alertName;
        @Range(min = 1, max=100)
        private int pageSize;
        @Min(1)
        private int page;

        public Shay() {
        }



        public String getAlertName() {
            return alertName;
        }

        public void setAlertName(String alertName) {
            this.alertName = alertName;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

}
