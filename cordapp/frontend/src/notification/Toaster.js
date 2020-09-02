import React from "react";
import "react-notifications-component/dist/theme.css";
import { store } from "react-notifications-component";
import "../assets/animate.css";

function UseToaster(title = "", message = "", type = "") {
    store.addNotification({
        title: title,
        message: message,
        type: type,
        insert: "top",
        container: "top-right",
        animationIn: ["animated", "fadeIn"],
        animationOut: ["animated", "fadeOut"],
        dismiss: {
          duration: 7000,
          onScreen: true
        }
    })
}
export default UseToaster;