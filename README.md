# Shopping Basket - Price Calculator

A Scala command-line application that calculates shopping basket prices with special offers.

## Products & Prices

| Product | Price |
|---------|-------|
| Soup    | 65p per tin |
| Bread   | 80p per loaf |
| Milk    | £1.30 per bottle |
| Apples  | £1.00 per bag |

## Special Offers

- **Apples**: 10% discount this week
- **Bread**: Buy 2 tins of soup, get bread for half price

## Prerequisites

First, check if Java and sbt are already installed and verify their versions:

```bash
java -version    # Should show Java 11 or higher
sbt --version    # Should show sbt 1.8.0 or higher
```

If Java version is below 11 or sbt is below 1.8.0 (or not installed), follow the installation instructions below:

### macOS
If you don't have Homebrew installed, install it from [brew.sh](https://brew.sh/).

```bash
# Install Java
brew install openjdk@11

# Install sbt
brew install sbt
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-11-jdk
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
sudo apt-get update && sudo apt-get install sbt
```

### Linux (CentOS/RHEL/Fedora)
```bash
sudo yum install java-11-openjdk-devel sbt  # or dnf on Fedora
```

### Windows
1. Download JDK from [Adoptium](https://adoptium.net/)
2. Download sbt from [sbt website](https://www.scala-sbt.org/download.html)
3. Add both to your PATH

After installation, verify again:
```bash
java -version
sbt --version
```

## Installation

Clone the repository and navigate to the project directory:

```bash
git clone https://github.com/NikolaLatkovic/shopping-basket.git
cd shopping-basket
```

Dependencies will be downloaded automatically on first build.

## Building & Testing

```bash
sbt compile    # Compile the project
sbt test       # Run unit tests
```

## Code Coverage

The project uses [scoverage](https://github.com/scoverage/sbt-scoverage) for code coverage analysis:

```bash
sbt clean coverage test coverageReport
```

**Coverage Requirements:** Minimum 70% statement and branch coverage. Build will fail if coverage drops below this threshold.

Coverage reports are available at `target/scala-2.13/scoverage-report/index.html`.

## Running the Application

### Step 1: Build the JAR

First, build the executable JAR file:

```bash
sbt assembly
```

### Step 2: Run the Application

The `PriceBasket` script is included in the project. Run it from the project directory:

```bash
./PriceBasket Apples Milk Bread
```

**Optional:** To run from anywhere, add the script to your PATH:

```bash
# Create /usr/local/bin if it doesn't exist (macOS)
sudo mkdir -p /usr/local/bin

# Create symlink
sudo ln -s $(pwd)/PriceBasket /usr/local/bin/PriceBasket
```

**Note:** If `/usr/local/bin` doesn't exist on your system, you can use `/usr/bin` instead:
```bash
sudo ln -s $(pwd)/PriceBasket /usr/bin/PriceBasket
```

Then you can run `PriceBasket Apples Milk Bread` from any directory.

**Alternative (Development):** For development, use `sbt "run Apples Milk Bread"` (shows compilation messages).

## Usage Examples

**Example 1:** Basket with apples discount
```bash
PriceBasket Apples Milk Bread
```
Output:
```
Subtotal: £3.10
Apples 10% off: 10p
Total price: £3.00
```

**Example 2:** Basket with bread offer
```bash
PriceBasket Soup Soup Bread
```
Output:
```
Subtotal: £2.10
Bread 50% off: 40p
Total price: £1.70
```

**Example 3:** No offers
```bash
PriceBasket Milk
```
Output:
```
Subtotal: £1.30
(No offers available)
Total price: £1.30
```

## Project Structure

```
shopping-basket/
├── build.sbt                    # Build configuration
├── VERSION                      # Project version
├── README.md
├── .gitignore
├── .scalafmt.conf              # Code formatting configuration
├── PriceBasket                  # Executable script
├── project/
│   ├── build.properties        # sbt version
│   └── plugins.sbt             # sbt plugins
└── src/
    ├── main/scala/com/shoppingbasket/
    │   ├── domain/              # Domain models
    │   │   ├── Basket.scala     # Product and Basket
    │   │   ├── Offer.scala      # Offer trait and implementations
    │   │   └── Receipt.scala    # Receipt, Discount, and MoneyFormatter
    │   ├── data/                # Data definitions (easy to extend)
    │   │   ├── ProductCatalog.scala  # Product definitions
    │   │   └── OfferRegistry.scala    # Active offers
    │   ├── service/
    │   │   └── ShoppingService.scala  # Business logic
    │   └── PriceBasket.scala    # Main entry point
    └── test/scala/com/shoppingbasket/
        └── service/
            └── ShoppingServiceSpec.scala  # Unit tests
```

## Troubleshooting

- **"java: command not found"**: Install Java and add to PATH
- **"sbt: command not found"**: Install sbt and add to PATH
- **Tests fail**: Run `sbt clean compile test`
