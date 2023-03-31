//////
//////  VisionCameraOcrTest.swift
//////  VisionCameraOcr
//////
//////  Created by Luke Stadtler on 3/31/23.
//////  Copyright © 2023 Facebook. All rights reserved.
//////
//
//import XCTest
//import MLKitTextRecognition
//import MLKitTextRecognitionChinese
//import MLKitTextRecognitionDevanagari
//import MLKitTextRecognitionJapanese
//import MLKitTextRecognitionKorean
//
//final class OCRFrameProcessorPluginTest: XCTestCase {
//  let ocrFrameProcessorPlugin = OCRFrameProcessorPlugin()
//
//   override func setUpWithError() throws {
//       // Put setup code here. This method is called before the invocation of each test method in the class.
//   }
//
//   override func tearDownWithError() throws {
//       // Put teardown code here. This method is called after the invocation of each test method in the class.
//   }
//
//   func testGetTextRecognizerOptionsForCodeDefaultToEnglish() throws {
//       XCTAssertTrue(ocrFrameProcessorPlugin.getTextRecognizerOptionsForCode() is TextRecognizerOptions)
//   }
//
//   func testGetTextRecognizerOptionsForCodeEnglish() throws {
//       XCTAssertTrue(ocrFrameProcessorPlugin.getTextRecognizerOptionsForCode(languageCode: "eng") is TextRecognizerOptions)
//   }
//
//   func testGetTextRecognizerOptionsForCodeChinese() throws {
//       XCTAssertTrue(ocrFrameProcessorPlugin.getTextRecognizerOptionsForCode(languageCode: "chi") is ChineseTextRecognizerOptions)
//   }
//
//   func testGetTextRecognizerOptionsForCodeDevanagari() throws {
//       XCTAssertTrue(ocrFrameProcessorPlugin.getTextRecognizerOptionsForCode(languageCode: "hin") is DevanagariTextRecognizerOptions)
//       XCTAssertTrue(ocrFrameProcessorPlugin.getTextRecognizerOptionsForCode(languageCode: "san") is DevanagariTextRecognizerOptions)
//       XCTAssertTrue(ocrFrameProcessorPlugin.getTextRecognizerOptionsForCode(languageCode: "pra") is DevanagariTextRecognizerOptions)
//   }
//
//   func testGetTextRecognizerOptionsForCodeJapanese() throws {
//       XCTAssertTrue(ocrFrameProcessorPlugin.getTextRecognizerOptionsForCode(languageCode: "jpn") is JapaneseTextRecognizerOptions)
//   }
//
//   func testGetTextRecognizerOptionsForCodeKorean() throws {
//       XCTAssertTrue(ocrFrameProcessorPlugin.getTextRecognizerOptionsForCode(languageCode: "kor") is KoreanTextRecognizerOptions)
//   }
//
//}
