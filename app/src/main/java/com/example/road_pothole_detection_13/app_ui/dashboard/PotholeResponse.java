// File: PotholeResponse.java
/* TODO: Đây là thông tin phản hồi JSON từ server*/

package com.example.road_pothole_detection_13.app_ui.dashboard;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PotholeResponse {

    // Thuộc tính đại diện cho trạng thái của phản hồi
    // Dùng để ánh xạ tên thuộc tính trong JSON với tên biến trong Java.
    @SerializedName("status")
    private String status;

    // Thông điệp từ server
    @SerializedName("message")
    private String message;

    // Danh sách dữ liệu các ổ gà
    @SerializedName("data")
    private List<Pothole> data;

    // Pothole: Thông tin đại diện cho một ổ gà trong danh sách data
    /**
     * Phương thức getter để lấy danh sách các ổ gà từ phản hồi.
     * @return danh sách các ổ gà (List<Pothole>).
     */
    public List<Pothole> getData() {
        return data;
    }
}
