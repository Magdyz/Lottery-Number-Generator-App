import React from "react";
import { View, Text, Image, StyleSheet, Dimensions } from "react-native";

const { width } = Dimensions.get("window");
const scale = (size) => (width / 375) * size;

const TitleBox = (props) => {
  return (
    <View style={styles.backgroundBox}>
      <Image source={require("../assets/icon.png")} style={styles.icon} />
      <Text style={styles.text}>{props.title}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  text: {
    fontSize: scale(38),
    fontWeight: "bold",
    color: "#FFFFFF", // Changed to white for high contrast
    textShadowColor: "rgba(0, 0, 0, 0.2)",
    textShadowOffset: { width: 1, height: 1 },
    textShadowRadius: 3,
  },
  backgroundBox: {
    marginTop: scale(30),
    marginBottom: scale(15),
    display: "flex",
    alignItems: "center",
    width: "90%",
    flexDirection: "row",
    justifyContent: "center",
  },
  icon: {
    opacity: 0.9,
    width: scale(60),
    height: scale(60),
    marginRight: scale(10),
  },
});

export default TitleBox;
