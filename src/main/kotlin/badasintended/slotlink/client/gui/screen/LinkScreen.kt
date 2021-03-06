package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.ButtonWidget
import badasintended.slotlink.client.gui.widget.FilterSlotWidget
import badasintended.slotlink.init.Networks.LINK_SETTINGS
import badasintended.slotlink.screen.LinkScreenHandler
import badasintended.slotlink.util.buf
import badasintended.slotlink.util.c2s
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
open class LinkScreen<H : LinkScreenHandler>(h: H, inventory: PlayerInventory, title: Text) : ModScreen<H>(h, inventory, title) {

    private var priority = handler.priority
    private var blacklist = handler.blacklist

    override val baseTlKey: String
        get() = "container.slotlink.cable"

    override fun init() {
        super.init()

        val x = x + 7
        val y = y + titleY + 11

        for (i in 0 until 9) {
            addButton(FilterSlotWidget(handler, i, x + 3 * 18 + (i % 3) * 18, y + (i / 3) * 18))
        }

        addButton(ButtonWidget(x + 2 * 18, y + 2, 14, 14, tl("priority.up"))).apply {
            u = { 242 }
            v = { 0 }
            onPressed = {
                priority++
                sync()
            }
        }

        addButton(ButtonWidget(x + 2 * 18, y + 2 + 2 * 18, 14, 14, tl("priority.down"))).apply {
            u = { 242 }
            v = { 14 }
            onPressed = {
                priority--
                sync()
            }
        }

        addButton(ButtonWidget(x + 6 * 18 + 4, y + 20, 14, 14, tl("blacklist"))).apply {
            u = { 228 }
            v = { if (blacklist) 14 else 0 }
            onPressed = {
                blacklist = !blacklist
                sync()
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("blacklist.$blacklist"), x, y)
            }
        }

    }

    protected open fun sync() {
        val buf = buf().apply {
            writeVarInt(handler.syncId)
            writeVarInt(priority)
            writeBoolean(blacklist)
        }
        c2s(LINK_SETTINGS, buf)
    }

    override fun init(client: MinecraftClient, width: Int, height: Int) {
        super.init(client, width, height)

        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        super.drawForeground(matrices, mouseX, mouseY)

        textRenderer.draw(matrices, "$priority", 7 + 2 * 18f, titleY + 31f, 4210752)
    }

}
