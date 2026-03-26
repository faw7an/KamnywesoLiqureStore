import re

css_path = "/var/home/xaimoh/IdeaProjects/KamnywesoLiqureStore/src/main/resources/com/example/kamnywesoliqourstore/style.css"
fxml_path = "/var/home/xaimoh/IdeaProjects/KamnywesoLiqureStore/src/main/resources/com/example/kamnywesoliqourstore/admin/admin-dashboard-view.fxml"

with open(css_path, "r") as f:
    css = f.read()

# Replace the nav-button CSS
css_new = re.sub(r'\.nav-button \{.*?\.nav-button-active \{.*?\}', r'''.nav-button {
    -fx-background-color: transparent;
    -fx-text-fill: white;
    -fx-alignment: CENTER_LEFT;
    -fx-padding: 12px 20px;
    -fx-background-radius: 8px;
    -fx-font-weight: bold;
}
.nav-button SVGPath {
    -fx-stroke: white;
}
.nav-button:hover {
    -fx-background-color: #1E293B;
    -fx-cursor: hand;
}
.nav-button-active {
    -fx-background-color: #ffffff;
    -fx-text-fill: #0F172A;
    -fx-alignment: CENTER_LEFT;
    -fx-padding: 12px 20px;
    -fx-background-radius: 8px;
    -fx-font-weight: bold;
}
.nav-button-active SVGPath {
    -fx-stroke: #0F172A;
}''', css, flags=re.DOTALL)

with open(css_path, "w") as f:
    f.write(css_new)

with open(fxml_path, "r") as f:
    fxml = f.read()

# Remove inline stroke colors so CSS can take over
fxml = re.sub(r'stroke="#[0-9A-Fa-f]+"', '', fxml)

# Wrap SVG in StackPane for fixed width alignment
fxml = re.sub(r'<graphic>\s*<SVGPath', r'<graphic>\n                    <StackPane prefWidth="24" alignment="CENTER_LEFT"><SVGPath', fxml)
fxml = re.sub(r'</SVGPath>\s*</graphic>', r'</SVGPath></StackPane>\n                </graphic>', fxml)

with open(fxml_path, "w") as f:
    f.write(fxml)

print("Updated style.css and FXML")
