Param (
    [Parameter(Mandatory=$true)] [string] $SubscriptionId,
    [Parameter(Mandatory=$true)] [string] $ResourceGroupName,
    [Parameter(Mandatory=$true)] [ValidateSet("northeurope", "westeurope")] [string] $Location,
    [Parameter(Mandatory=$true)] [string] $ResourcePrefix
)

$Context = Get-AzureRmContext

if ($null -eq $Context) {
    Login-AzureRmAccount -SubscriptionId $SubscriptionId
} else {
    Select-AzureRmSubscription -SubscriptionId $SubscriptionId
}

$ResourceGroup = Get-AzureRmResourceGroup -Name $ResourceGroupName -ErrorAction SilentlyContinue

if ($null -eq $ResourceGroup) {
    $ResourceGroup = New-AzureRmResourceGroup -Name $ResourceGroupName -Location $Location
}

$Parameters = @{}
$Parameters.Add("resourcePrefix", "demo")
$Parameters.Add("storageAccountType", "Standard_LRS")
$Parameters.Add("appServicePlanSku", "S1")

Write-Host "Deploying resources" -ForegroundColor Green

$Deployment = New-AzureRmResourceGroupDeployment -Name ($ResourceGroupName + '-deployment-' + ((Get-Date).ToUniversalTime()).ToString('MMdd-HHmm')) `
                                                 -ResourceGroupName $ResourceGroupName `
                                                 -TemplateFile ".\templates\azuredeploy.json" `
                                                 -TemplateParameterObject $Parameters `
                                                 -Force `
                                                 -Verbose

$StorageAccountName = $Deployment.Outputs["storageAccountName"].Value

$StorageAccount = Get-AzureRmStorageAccount -ResourceGroupName $ResourceGroupName -Name $StorageAccountName
$SAContext = $StorageAccount.Context

Write-Host "Checking for existing storage account container" -ForegroundColor DarkYellow

$Container = Get-AzureStorageContainer -Name "example" -Context $SAContext -ErrorAction SilentlyContinue

if ($null -eq $Container) {
    Write-Host "Creating container" -ForegroundColor Green
    $Container = New-AzureStorageContainer -Name "example" -Context $SAContext
}