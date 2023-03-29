require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "vision-camera-ocr"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "11.0" }
  s.source       = { :git => "https://github.com/aarongrider/vision-camera-ocr.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift}"

  s.dependency "React-Core"
  s.dependency "GoogleMLKit/TextRecognition", "3.2.0"
  # To recognize Chinese script
  s.dependency "GoogleMLKit/TextRecognitionChinese", "3.2.0"
  # To recognize Devanagari script
  s.dependency "GoogleMLKit/TextRecognitionDevanagari", "3.2.0"
  # To recognize Japanese script
  s.dependency "GoogleMLKit/TextRecognitionJapanese", "3.2.0"
  # To recognize Korean script
  s.dependency "GoogleMLKit/TextRecognitionKorean", "3.2.0"
end
