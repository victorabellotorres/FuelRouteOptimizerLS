# Run experiments to compare initial-state generators and search algorithms
# Usage: .\run_experiments.ps1 -iterations 10 -seeds 1234,4321 -out results.csv
param(
    [int]$iterations = 5,
    [string]$seeds = "1234",
    [string]$out = "experiment_results.csv",
    [int[]]$initialOptions = @(1,2,3,4,5), # 1=Orden,2=Aleatorio,3=Greedy-Distancia,4=Greedy-Quadrants,5=Greedy-X
    [int[]]$algOptions = @(1,2) # 1=HillClimbing,2=SimulatedAnnealing
)

# Classpath
$base = Split-Path -Parent $MyInvocation.MyCommand.Definition
 $aima = Join-Path $base "libs\AIMA.jar"
 $gas = Join-Path $base "libs\Gasolina.jar"
 $bin = Join-Path $base "bin"
 $cp = "$aima;$gas;$bin"

# Compile
Write-Host "Compiling Java sources..."
$src = Get-ChildItem -Recurse -Filter *.java -Path (Join-Path $base "src") | ForEach-Object { $_.FullName }
javac -d bin -cp $cp $src
if ($LASTEXITCODE -ne 0) { Write-Error "Compilation failed"; exit 1 }
Write-Host "Compilation OK"

# Prepare output (columns: initial and final heuristic, assigned petitions)
"seed,iteration,initial,algorithm,timeMs,initialHeur,finalHeur,assigned,nodesExpanded" | Out-File $out -Encoding utf8

$seedList = $seeds -split ","
foreach ($seed in $seedList) {
    for ($it = 1; $it -le $iterations; $it++) {
        foreach ($init in $initialOptions) {
            foreach ($alg in $algOptions) {
                Write-Host "Running seed=$seed it=$it init=$init alg=$alg..."
                $input = @($init.ToString(), $alg.ToString(), $seed.ToString(), "10", "100", "1")
                $inputStr = $input -join "`n"
                $start = Get-Date
                $result = $inputStr | & java -cp $cp main.Main 2>&1
                $end = Get-Date
                $ms = ($end - $start).TotalMilliseconds

                # Parse initial/final heuristic and nodesExpanded from output (tolerant to accents)
                $initialHeur = ""
                $finalHeur = ""
                $assigned = ""
                $nodes = ""
                foreach ($line in $result -split "`n") {
                    if ($line -match ".*inicial:\s*(-?[0-9]+\.?[0-9]*)") { $initialHeur = $Matches[1] }
                    if ($line -match ".*final:\s*(-?[0-9]+\.?[0-9]*)") { $finalHeur = $Matches[1] }
                    if ($line -match "Peticiones inicialment assignades:\s*([0-9]+)/([0-9]+)") { $assigned = $Matches[1] + "/" + $Matches[2] }
                    if ($line -match "nodesExpanded\s*:\s*([0-9]+)") { $nodes = $Matches[1] }
                }

                if (-not $finalHeur) { $finalHeur = "NA" }
                if (-not $initialHeur) { $initialHeur = "NA" }
                if (-not $assigned) { $assigned = "NA" }
                if (-not $nodes) { $nodes = "NA" }

                # Save: seed,iteration,initial,algorithm,timeMs,initialHeur,finalHeur,assigned,nodes
                "$seed,$it,$init,$alg,$ms,$initialHeur,$finalHeur,$assigned,$nodes" | Out-File $out -Append -Encoding utf8
            }
        }
    }
}
Write-Host "All experiments finished. Results in $out" 
