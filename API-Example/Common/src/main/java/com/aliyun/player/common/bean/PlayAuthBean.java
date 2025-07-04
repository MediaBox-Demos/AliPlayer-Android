package com.aliyun.player.common.bean;

/**
 * @author junhuiYe
 * @date 2025/6/26 13:58
 * @brief playAuth数据实体类
 */
public class PlayAuthBean {
    private String result;
    private String requestId;
    private String message;
    private String code;
    private DataBean data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String requestId;
        private String playAuth;
        private VideoMetaBean videoMeta;

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getPlayAuth() {
            return playAuth;
        }

        public void setPlayAuth(String playAuth) {
            this.playAuth = playAuth;
        }

        public VideoMetaBean getVideoMeta() {
            return videoMeta;
        }

        public void setVideoMeta(VideoMetaBean videoMeta) {
            this.videoMeta = videoMeta;
        }

        public static class VideoMetaBean {
            /**
             * coverURL : https://alivc-demo-vod.aliyuncs.com/image/cover/E972F03B53494559A4608F67AA224FDF-6-2.png
             * duration : 114.103
             * status : Normal
             * title : title
             * videoId : 70a84eee21cd71f0bfee5017f1e90102
             */
            private String coverURL;
            private double duration;
            private String status;
            private String title;
            private String videoId;

            public String getCoverURL() {
                return coverURL;
            }

            public void setCoverURL(String coverURL) {
                this.coverURL = coverURL;
            }

            public double getDuration() {
                return duration;
            }

            public void setDuration(double duration) {
                this.duration = duration;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getVideoId() {
                return videoId;
            }

            public void setVideoId(String videoId) {
                this.videoId = videoId;
            }
        }
    }
}
