import React, { useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  Modal,
  StyleSheet,
  Dimensions,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";

const { width } = Dimensions.get("window");
const scale = (size) => (width / 375) * size;

const HelpModal = () => {
  const [isModalVisible, setModalVisible] = useState(false);

  const toggleModal = () => {
    setModalVisible(!isModalVisible);
  };

  return (
    <>
      <TouchableOpacity onPress={toggleModal} style={styles.iconContainer}>
        <Ionicons
          name="help-circle-outline"
          size={scale(38)}
          color={"#FFFFFF"} // Changed to white for high contrast
        />
      </TouchableOpacity>

      <Modal
        visible={isModalVisible}
        animationType="fade"
        transparent={true}
        onRequestClose={toggleModal}
      >
        <TouchableOpacity
          style={styles.modalOverlay}
          activeOpacity={1}
          onPressOut={toggleModal}
        >
          <View style={styles.modalContent}>
            <Text style={styles.modalText}>
              Need help? Simply tap any of the three buttons to get a random set
              of numbers for Euro Millions, Lotto, and Set for Life.
            </Text>
            <Text style={styles.modalText}>
              No more relying on quick picks or lucky dips - take control of
              your lottery destiny!
            </Text>
            <TouchableOpacity style={styles.closeIcon} onPress={toggleModal}>
              <Ionicons
                name="close-circle-outline"
                size={scale(40)}
                color="#555"
              />
            </TouchableOpacity>
          </View>
        </TouchableOpacity>
      </Modal>
    </>
  );
};

const styles = StyleSheet.create({
  iconContainer: {
    position: "absolute",
    bottom: scale(25),
    right: scale(25),
    zIndex: 10,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.3,
    shadowRadius: 2,
  },
  modalOverlay: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(0, 0, 0, 0.5)",
  },
  modalContent: {
    width: width * 0.9,
    backgroundColor: "rgba(255, 255, 255, 0.9)",
    borderRadius: scale(20),
    padding: scale(20),
    paddingTop: scale(40),
    alignItems: "center",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },
  closeIcon: {
    position: "absolute",
    top: scale(10),
    right: scale(10),
  },
  modalText: {
    fontSize: scale(18),
    textAlign: "center",
    marginBottom: scale(15),
    color: "#333",
  },
});

export default HelpModal;
