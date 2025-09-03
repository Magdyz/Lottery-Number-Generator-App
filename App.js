import React from "react";
import {
  View,
  SafeAreaView,
  StyleSheet,
  StatusBar,
  ScrollView,
} from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import TitleBox from "./src/TitleBox";
import ButtonFunction from "./src/ButtonFunction";
import HelpModal from "./src/HelpModal";

const App = () => {
  return (
    <SafeAreaView style={styles.mainContainer}>
      {/* Status bar text is now light to contrast with the dark background */}
      <StatusBar barStyle="light-content" />
      <LinearGradient
        colors={["#2C3E50", "#4CA1AF"]} // New modern, dark teal gradient
        style={styles.container}
      >
        <ScrollView contentContainerStyle={styles.scrollContentContainer}>
          <TitleBox title="Lucky Generator" />
          <View>
            <ButtonFunction
              buttonTitle="EUROMILLIONS"
              firstNumberOfTimes="5"
              firstNumbers="50"
              secondNumberOfTimes="2"
              secondNumber="12"
            />
            <ButtonFunction
              buttonTitle="LOTTO"
              firstNumberOfTimes="6"
              firstNumbers="59"
              secondNumberOfTimes="0"
              secondNumber="0"
            />
            <ButtonFunction
              buttonTitle="SET FOR LIFE"
              firstNumberOfTimes="5"
              firstNumbers="47"
              secondNumberOfTimes="1"
              secondNumber="10"
            />
          </View>
        </ScrollView>
        <HelpModal />
      </LinearGradient>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  mainContainer: {
    flex: 1,
    backgroundColor: "#2C3E50", // Match top of gradient for notch area
  },
  container: {
    flex: 1,
  },
  scrollContentContainer: {
    alignItems: "center",
    paddingBottom: 20,
  },
});

export default App;
