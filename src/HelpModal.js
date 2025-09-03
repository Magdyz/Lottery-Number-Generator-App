import React, { useState } from "react";
import { View, Text, TouchableOpacity, Modal, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";

const HelpModal = () => {
  const [isModalVisible, setModalVisible] = useState(false);

  const toggleModal = () => {
    setModalVisible(!isModalVisible);
  };

  return (
    <View style={styles.iconContiner}>
      <TouchableOpacity onPress={toggleModal}>
        {/* Changed from "ios-help-circle-outline" */}
        <Ionicons
          name="help-circle-outline"
          size={35}
          color={"#023e8a"}
        ></Ionicons>
      </TouchableOpacity>
      <Modal visible={isModalVisible} animationType="fade">
        <View style={styles.modal}>
          {
            <View>
              <Text style={styles.modalText}>
                {" "}
                Need help using our lottery number generator app? No problem!
                Simply tap any of the three buttons to get a random set of
                numbers for Euro Millions, Lotto, and Set for Life. Use the app
                as many times as you want to generate your own lucky numbers.
              </Text>
              <Text style={styles.modalText}>
                {" "}
                No more relying on quick picks or lucky dips - take control of
                your lottery destiny!
              </Text>
            </View>
          }
          <TouchableOpacity style={styles.closeIcon} onPress={toggleModal}>
            {/* Changed from "ios-close-circle-outline" */}
            <Ionicons name="close-circle-outline" size={35}></Ionicons>
          </TouchableOpacity>
        </View>
      </Modal>
    </View>
  );
};

const styles = StyleSheet.create({
  modal: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#ebdb65",
    padding: 20,
    color: "#003566",
  },
  iconContiner: {
    position: "absolute",
    top: "85%",
    right: "4%",
    opacity: 0.3,
  },
  closeIcon: {
    position: "absolute",
    top: "7%",
    right: "7%",
  },
  modalText: {
    fontSize: 25,
    padding: 5,
    textAlign: "center",
  },
});

export default HelpModal;
